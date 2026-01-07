package com.uncreated.civilized.entity;

import static com.uncreated.civilized.entity.behaviour.CivilizedVillagerActivities.*;
import static com.uncreated.civilized.entity.behaviour.worker.WorkActivities.getWorkPackage;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Dynamic;
import com.uncreated.civilized.core.StoreOperation;
import com.uncreated.civilized.core.dialogue.IVillageDialogue;
import com.uncreated.civilized.core.dialogue.context.DialogueContext;
import com.uncreated.civilized.core.dialogue.controller.DialogueController;
import com.uncreated.civilized.core.dialogue.controller.DialogueFlow;
import com.uncreated.civilized.core.dialogue.specialized.ItemDepotDialogue;
import com.uncreated.civilized.core.villagerinfo.ClientVillagerStore;
import com.uncreated.civilized.core.villagerinfo.ServerVillagerStore;
import com.uncreated.civilized.core.villagerinfo.VillagerInfo;
import com.uncreated.civilized.core.villagerinfo.VillagerOccupation;
import com.uncreated.civilized.entity.pathfinding.VillagerGroundPathNavigation;
import com.uncreated.civilized.entity.stats.ClothingTextureRegistry;
import com.uncreated.civilized.entity.stats.HairTextureRegistry;
import com.uncreated.civilized.entity.stats.SkinTextureRegistry;
import com.uncreated.civilized.neoforge.registration.ai.AIRegistry;
import com.uncreated.civilized.ui.menu.dialogue.VillagerDialogueScreen;

import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.npc.InventoryCarrier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.entity.IEntityWithComplexSpawn;

public class CivilizedVillager extends AgeableMob implements InventoryCarrier, IEntityWithComplexSpawn {

   private static final Logger LOGGER = LogUtils.getLogger();
   public static final String FIELD_VILLAGER_ID = "villager_id";
   public static final String FIELD_LIFETIME_SEED = "lifetime_seed";

   @Getter
   private UUID villagerId;

   @Getter
   private VillagerInfo info;

   @Getter
   private DialogueController dialogueController = DialogueController.noDialogue();

   private final SimpleContainer workInputInventory = new SimpleContainer(8);
   private final SimpleContainer workOutputInventory = new SimpleContainer(8);
   private final SimpleContainer logisticsInventory = new SimpleContainer(8);

   private long lifetimeSeed;

   @Getter
   private final RandomSource lifetimeRandom;

   public CivilizedVillager(EntityType<? extends AgeableMob> entityType, Level level) {
      super(entityType, level);
      ((VillagerGroundPathNavigation) this.getNavigation()).setCanOpenDoors(true);
      this.getNavigation().setCanFloat(true);
      this.getNavigation().setRequiredPathLength(48.0F);
      // this.setCanPickUpLoot(true);
      this.lifetimeRandom = RandomSource.create();
      this.setPersistenceRequired(); // prevent auto-despawning
   }

   public void initBrandNewVillager() {
      info = ServerVillagerStore.INSTANCE.createNewVillager(this);
      ServerVillagerStore.INSTANCE.replicateChange(info, StoreOperation.ADD_OR_OVERWRITE);
      ServerVillagerStore.INSTANCE.setDirty();
      villagerId = info.getVillagerId();
      setLifetimeRandom(getRandom().nextLong());
   }

   public void initVillagerFromSave() {
      info = ServerVillagerStore.INSTANCE.get(villagerId);
   }

   public void serverFinalizeSpawn() {
      refreshBrain((ServerLevel) level());

      dialogueController = DialogueController.selectDialogueController(this);
   }

   private void setLifetimeRandom(long seed) {
      lifetimeSeed = seed;
      lifetimeRandom.setSeed(seed);
   }

   public RandomSource getConsistentLifetimeRandom() {
      return RandomSource.create(lifetimeSeed);
   }

   @Override
   public void addAdditionalSaveData(CompoundTag compound) {
      super.addAdditionalSaveData(compound);

      compound.putUUID(FIELD_VILLAGER_ID, villagerId);
      compound.putLong(FIELD_LIFETIME_SEED, lifetimeSeed);
      this.writeInventoryToTag(compound, this.registryAccess());
   }

   @Override
   public void readAdditionalSaveData(CompoundTag compound) {
      super.readAdditionalSaveData(compound);
      // when spawning a new villager, readAdditionalSaveData will still run, but the NBT data won't have our fields
      // yet, so we need to check
      if (compound.hasUUID(FIELD_VILLAGER_ID))
         villagerId = compound.getUUID(FIELD_VILLAGER_ID);
      setLifetimeRandom(compound.getLong(FIELD_LIFETIME_SEED));

      this.readInventoryFromTag(compound, this.registryAccess());
   }

   @Override
   public void writeSpawnData(RegistryFriendlyByteBuf buf) {
      info.encode(buf);
      buf.writeLong(lifetimeSeed);
   }

   public void readInventoryFromTag(CompoundTag tag, HolderLookup.Provider levelRegistry) {
      if (tag.contains("Inventory", 9)) {
         workOutputInventory.fromTag(tag.getList("Inventory", 10), levelRegistry);
      }
      if (tag.contains("WorkInput", 9)) {
         workInputInventory.fromTag(tag.getList("WorkInput", 10), levelRegistry);
      }
      if (tag.contains("Logistics", 9)) {
         logisticsInventory.fromTag(tag.getList("Logistics", 10), levelRegistry);
      }

   }

   public void writeInventoryToTag(CompoundTag tag, HolderLookup.Provider levelRegistry) {
      tag.put("Inventory", workOutputInventory.createTag(levelRegistry));
      tag.put("WorkInput", workInputInventory.createTag(levelRegistry));
      tag.put("Logistics", logisticsInventory.createTag(levelRegistry));
   }

   @Getter
   private ResourceLocation skin = SkinTextureRegistry.FALLBACK;
   @Getter
   private ResourceLocation hair = SkinTextureRegistry.FALLBACK;
   @Getter
   private ResourceLocation clothing = ClothingTextureRegistry.FALLBACK;

   @Override
   public void readSpawnData(RegistryFriendlyByteBuf buf) {
      info = ClientVillagerStore.INSTANCE.addFromServer(VillagerInfo.decode(buf));
      lifetimeSeed = buf.readLong();
      dialogueController = DialogueController.selectDialogueController(this);
      ClientVillagerStore.INSTANCE.addFromServer(info);
      villagerId = info.getVillagerId();
      updateSkin();
      updateClothing();
   }

   public void updateSkin() {
      skin = SkinTextureRegistry.getRandomSkin(getConsistentLifetimeRandom(), "default", info.getGender()).getValue();
      hair = HairTextureRegistry.getRandomSkin(getConsistentLifetimeRandom(), "default", info.getGender()).getValue();
   }

   public void updateClothing() {
      clothing =
            ClothingTextureRegistry
                  .getRandomClothingTexture(
                        getConsistentLifetimeRandom(),
                        "default",
                        info.getOccupation(),
                        info.getGender())
                  .getValue();
   }

   @Override
   public @NotNull SimpleContainer getInventory() {
      return workOutputInventory;
   }

   public @NotNull SimpleContainer getWorkInputInventory() {
      return workInputInventory;
   }

   public @NotNull SimpleContainer getWorkOutputInventory() {
      return workOutputInventory;
   }

   public @NotNull SimpleContainer getLogisticsInventory() {
      return logisticsInventory;
   }

   @Override
   public @Nullable AgeableMob getBreedOffspring(ServerLevel serverLevel, AgeableMob ageableMob) {
      return null;
   }

   @Override
   public HumanoidArm getMainArm() {
      return this.isLeftHanded() ? HumanoidArm.LEFT : HumanoidArm.RIGHT;
   }

   @Override
   public InteractionResult mobInteract(Player player, InteractionHand hand) {

      dialogueController = DialogueController.selectDialogueController(this);
      DialogueFlow dialogueFlow = dialogueController.getDialogueFlow(this, player, hand);

      if (dialogueFlow == null)
         return InteractionResult.PASS;

      DialogueContext context = dialogueFlow.buildDialogueContext(this, player, hand);
      IVillageDialogue dialogue = dialogueFlow.getOpeningDialogue(context);

      while (dialogue != null && !dialogue.isAvailableToPlayer(context)) {
         dialogue = dialogue.getFallback();
      }

      if (dialogue == null) {
         return InteractionResult.PASS;
      }
      if (dialogue instanceof ItemDepotDialogue itemDepotDialogue) {

         ItemStack itemInHand = player.getItemInHand(hand);
         if (itemInHand.isEmpty())
            return InteractionResult.PASS;

         if (!itemDepotDialogue.getCanConsumeItemCheck().canConsumeItem(context, itemInHand, hand))
            return InteractionResult.PASS;

         ItemStack consumed = itemDepotDialogue.getConsumeItemAction().consumeItem(context, itemInHand, hand);
         return InteractionResult.CONSUME.heldItemTransformedTo(consumed);
      }

      if (player.level().isClientSide) {
         VillagerDialogueScreen screen = new VillagerDialogueScreen(this, dialogue, context);
         Minecraft.getInstance().setScreen(screen);
      }
      return InteractionResult.SUCCESS;
   }

   @Override
   protected PathNavigation createNavigation(Level level) {
      return new VillagerGroundPathNavigation(this, level);
   }

   @Override
   public Brain<CivilizedVillager> getBrain() {
      return (Brain<CivilizedVillager>) super.getBrain();
   }

   @Override
   protected Brain.Provider<CivilizedVillager> brainProvider() {
      return Brain.provider(
            List.of(
                  AIRegistry.MM_CROP_FIELD_CENTER.get(),
                  AIRegistry.MM_VILLAGER_WORKTIME_OCCUPATION.get(),
                  AIRegistry.MM_DIALOGUE_TARGET.get(),
                  AIRegistry.MM_HAS_NON_IDLE_WORK_TASK.get(),
                  AIRegistry.MM_HAS_WORK_INPUT_RESOURCES.get(),
                  AIRegistry.MM_HAS_WORK_OUTPUT_RESOURCES.get(),
                  AIRegistry.MM_BUSY_OFFLOADING_IMPORTS.get(),
                  AIRegistry.MM_BUSY_OFFLOADING_EXPORTS.get(),
                  AIRegistry.MM_EXPORT_DESIRED.get(),
                  AIRegistry.MM_IMPORT_DESIRED.get(),
                  MemoryModuleType.JOB_SITE,
                  MemoryModuleType.HOME,
                  MemoryModuleType.PATH,
                  MemoryModuleType.WALK_TARGET,
                  MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE,
                  MemoryModuleType.LOOK_TARGET,
                  MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES,
                  MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES,
                  MemoryModuleType.NEAREST_PLAYERS,
                  MemoryModuleType.NEAREST_VISIBLE_PLAYER,
                  MemoryModuleType.DOORS_TO_CLOSE,
                  MemoryModuleType.INTERACTION_TARGET),
            List.of(
                  AIRegistry.S_CROP_BLOCK.get(),
                  SensorType.NEAREST_LIVING_ENTITIES,
                  SensorType.NEAREST_PLAYERS,
                  SensorType.NEAREST_ITEMS));
   }

   // TECHDEBT: since this is called in constructor, registerBrainGoals cannot be called here because it depends on
   // villagerInfo
   @Override
   protected Brain<?> makeBrain(Dynamic<?> dynamic) {
      return this.brainProvider().makeBrain(dynamic);
   }

   private void registerBrainGoals(Brain<CivilizedVillager> brain) {
      brain.setSchedule(AIRegistry.SCHED_CIVILIZED_VILLAGER_DEFAULT.get());
      brain.addActivity(Activity.CORE, getCorePackage(0.33f));
      brain.addActivity(Activity.IDLE, getIdlePackage(0.25f));
      if (info.getOccupation() != VillagerOccupation.UNEMPLOYED) {
         brain.addActivityWithConditions(
               Activity.WORK,
               getWorkPackage(info.getOccupation()),
               Set.of(Pair.of(AIRegistry.MM_VILLAGER_WORKTIME_OCCUPATION.get(), MemoryStatus.VALUE_PRESENT)));
      }
      brain.addActivityWithConditions(
            Activity.REST,
            getRestPackage(0.4F),
            Set.of(Pair.of(MemoryModuleType.HOME, MemoryStatus.VALUE_PRESENT)));
      brain.addActivityAndRemoveMemoriesWhenStopped(
            AIRegistry.A_SPEAK_TO_PLAYER.get(),
            getSpeakToPlayerPackage(),
            Set.of(Pair.of(AIRegistry.MM_DIALOGUE_TARGET.get(), MemoryStatus.VALUE_PRESENT)),
            Set.of(AIRegistry.MM_DIALOGUE_TARGET.get()));
      brain.setCoreActivities(ImmutableSet.of(Activity.CORE));
      brain.setDefaultActivity(Activity.IDLE);
      brain.setActiveActivityIfPossible(Activity.IDLE);
      brain.updateActivityFromSchedule(this.level().getDayTime(), this.level().getGameTime());
   }

   public void refreshBrain(ServerLevel serverLevel) {
      Brain<CivilizedVillager> brain = this.getBrain();
      brain.stopAll(serverLevel, this);
      this.brain = brain.copyWithoutBehaviors();
      this.registerBrainGoals(this.getBrain());
   }

   @Override
   public void aiStep() {
      this.updateSwingTime();
      super.aiStep();
   }

   @Override
   protected void customServerAiStep(ServerLevel serverLevel) {
      ProfilerFiller profilerFiller = Profiler.get();
      profilerFiller.push("civilizedVillagerBrain");
      this.getBrain().tick(serverLevel, this);
      profilerFiller.pop();

      super.customServerAiStep(serverLevel);
   }

   public void goSpeakToPlayer(Player player) {
      getBrain().setMemory(AIRegistry.MM_DIALOGUE_TARGET.get(), player);
      getBrain().setActiveActivityIfPossible(AIRegistry.A_SPEAK_TO_PLAYER.get());
   }

   public void stopSpeakingToPlayer() {
      getBrain().eraseMemory(AIRegistry.MM_DIALOGUE_TARGET.get());
      brain.setActiveActivityIfPossible(Activity.IDLE);
   }
}
