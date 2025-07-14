package com.uncreated.civilized.entity;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Dynamic;
import com.uncreated.civilized.core.building.Building;
import com.uncreated.civilized.core.building.ServerBuildingsStore;
import com.uncreated.civilized.core.dialogue.DialoguePackage;
import com.uncreated.civilized.core.dialogue.IVillageDialogue;
import com.uncreated.civilized.core.dialogue.traveler.QuestStayAtVillage;
import com.uncreated.civilized.core.dialogue.traveler.quest.JoinSettlementContext;
import com.uncreated.civilized.core.villagerinfo.ClientVillagerStore;
import com.uncreated.civilized.core.villagerinfo.ServerVillagerStore;
import com.uncreated.civilized.core.villagerinfo.VillagerInfo;
import com.uncreated.civilized.entity.behaviour.InvalidateImportantLocations;
import com.uncreated.civilized.entity.behaviour.LongDistanceTravelToRememberedPos;
import com.uncreated.civilized.entity.behaviour.OffloadResourcesAtHome;
import com.uncreated.civilized.entity.behaviour.UpdateActivityFromSchedule;
import com.uncreated.civilized.entity.behaviour.farmer.HarvestCrops;
import com.uncreated.civilized.neoforge.registration.ai.AIRegistry;
import com.uncreated.civilized.neoforge.registration.entity.EntityRegistry;
import com.uncreated.civilized.ui.menu.dialogue.VillagerDialogueScreen;

import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.core.GlobalPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.behavior.DoNothing;
import net.minecraft.world.entity.ai.behavior.InteractWith;
import net.minecraft.world.entity.ai.behavior.InteractWithDoor;
import net.minecraft.world.entity.ai.behavior.LookAtTargetSink;
import net.minecraft.world.entity.ai.behavior.MoveToTargetSink;
import net.minecraft.world.entity.ai.behavior.RunOne;
import net.minecraft.world.entity.ai.behavior.SetEntityLookTarget;
import net.minecraft.world.entity.ai.behavior.SetLookAndInteract;
import net.minecraft.world.entity.ai.behavior.SetWalkTargetFromLookTarget;
import net.minecraft.world.entity.ai.behavior.StrollAroundPoi;
import net.minecraft.world.entity.ai.behavior.StrollToPoi;
import net.minecraft.world.entity.ai.behavior.Swim;
import net.minecraft.world.entity.ai.behavior.VillageBoundRandomStroll;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.npc.InventoryCarrier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.entity.IEntityWithComplexSpawn;

public class CivilizedVillager extends AgeableMob implements InventoryCarrier, IEntityWithComplexSpawn {

   private static final Logger LOGGER = LogUtils.getLogger();
   public static final String FIELD_VILLAGER_ID = "villager_id";

   @Getter
   private UUID villagerId;

   @Getter
   private VillagerInfo info;

   private final SimpleContainer inventory = new SimpleContainer(8);

   public CivilizedVillager(EntityType<? extends AgeableMob> entityType, Level level) {
      super(entityType, level);
   }

   public void syncVillagerInfo() {

      if (!level().isClientSide) {
         if (villagerId == null)
            villagerId = UUID.randomUUID(); // TECHDEBT: find a more reliable place to set villagerId for the first time

         info = ServerVillagerStore.INSTANCE.getOrAdd(this);

         if (!info.hasName()) {
            Pair<String, String> newName = VillagerInfo.generateRandomName();
            info.setFirstName(newName.getFirst());
            info.setLastName(newName.getSecond());
         }
      }
   }

   @Override
   public void addAdditionalSaveData(CompoundTag compound) {
      super.addAdditionalSaveData(compound);

      if (villagerId != null)
         compound.putUUID(FIELD_VILLAGER_ID, villagerId);

      this.writeInventoryToTag(compound, this.registryAccess());
   }

   @Override
   public void readAdditionalSaveData(CompoundTag compound) {
      super.readAdditionalSaveData(compound);
      if (compound.hasUUID(FIELD_VILLAGER_ID))
         villagerId = compound.getUUID(FIELD_VILLAGER_ID);
      else
         villagerId = UUID.randomUUID();

      this.readInventoryFromTag(compound, this.registryAccess());
   }

   @Override
   public void writeSpawnData(RegistryFriendlyByteBuf buf) {
      info.encode(buf);
   }

   @Override
   public void readSpawnData(RegistryFriendlyByteBuf buf) {
      info = VillagerInfo.decode(buf);
      ClientVillagerStore.INSTANCE.addFromServer(info);
      villagerId = info.getVillagerId();
   }

   @Override
   public @NotNull SimpleContainer getInventory() {
      return inventory;
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
   public InteractionResult mobInteract(Player player, InteractionHand interactionHand) {

      if (player.level().isClientSide) {
         DialoguePackage dialoguePackage = QuestStayAtVillage.questStayAtVillage();
         IVillageDialogue dialogue = dialoguePackage.chooseRandom();
         VillagerDialogueScreen screen =
               new VillagerDialogueScreen(this, dialogue, new JoinSettlementContext(this, player));
         Minecraft.getInstance().setScreen(screen);
      }
      return InteractionResult.SUCCESS;
   }

   public void invalidateHomeAndJobMemories() {

      if (info.getHomeBuildingId() != null) {
         Building home = ServerBuildingsStore.INSTANCE.get(info.getHomeBuildingId());
         getBrain().setMemory(MemoryModuleType.HOME, new GlobalPos(level().dimension(), home.getBlockPos()));
      }
      getBrain().setMemory(AIRegistry.MM_VILLAGER_OCCUPATION.get(), info.getOccupation());
      // probably ok to not invalidate workpos here
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
                  AIRegistry.MM_CAN_OFFLOAD.get(),
                  AIRegistry.MM_VILLAGER_OCCUPATION.get(),
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

   @Override
   protected Brain<?> makeBrain(Dynamic<?> dynamic) {
      Brain<CivilizedVillager> brain = this.brainProvider().makeBrain(dynamic);
      this.registerBrainGoals(brain);
      return brain;
   }

   private void registerBrainGoals(Brain<CivilizedVillager> brain) {

      brain.setSchedule(AIRegistry.SCHED_CIVILIZED_VILLAGER_DEFAULT.get());
      brain.addActivity(Activity.CORE, getCorePackage(0.33f));
      brain.addActivity(Activity.IDLE, getIdlePackage(0.25f));
      brain.addActivityWithConditions(
            Activity.WORK,
            getWorkPackage(),
            Set.of(Pair.of(AIRegistry.MM_VILLAGER_OCCUPATION.get(), MemoryStatus.VALUE_PRESENT)));
      brain.addActivityWithConditions(
            Activity.REST,
            getRestPackage(0.4F),
            Set.of(Pair.of(MemoryModuleType.HOME, MemoryStatus.VALUE_PRESENT)));
      brain.setCoreActivities(ImmutableSet.of(Activity.CORE));
      brain.setDefaultActivity(Activity.IDLE);
      brain.setActiveActivityIfPossible(Activity.IDLE);
      brain.updateActivityFromSchedule(this.level().getDayTime(), this.level().getGameTime());
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

   public void invalidateVillagerMemories() {

   }

   public static ImmutableList<Pair<Integer, ? extends BehaviorControl<CivilizedVillager>>> getWorkPackage() {
      return ImmutableList.of(
            getMinimalLookBehavior(),
            Pair.of(
                  1,
                  new RunOne<>(
                        ImmutableList.of(
                              // go to work. closeEnoughDist should +1 more StrollAroundPoi's maxDistFromPoi.
                              Pair.of(StrollToPoi.create(MemoryModuleType.JOB_SITE, 0.4F, 5, 100), 2),
                              Pair.of(new HarvestCrops(), 4),
                              Pair.of(new OffloadResourcesAtHome(), 5),
                              // if cannot perform main work tasks, stroll around the job site.
                              Pair.of(StrollAroundPoi.create(MemoryModuleType.JOB_SITE, 0.25F, 4), 6)))),
            Pair.of(99, UpdateActivityFromSchedule.create()));
   }

   public static ImmutableList<Pair<Integer, ? extends BehaviorControl<? super CivilizedVillager>>> getRestPackage(
         float speedModifier) {
      return ImmutableList.of(
            Pair.of(2, LongDistanceTravelToRememberedPos.create(MemoryModuleType.HOME, speedModifier, 1, 300, 1500)),
            // Pair.of(3, ValidateNearbyPoi.create((p_217495_) -> p_217495_.is(PoiTypes.HOME), MemoryModuleType.HOME)),
            // Pair.of(3, new SleepInBed()),
            Pair.of(
                  5,
                  new RunOne( // for now, do nothing at home
                        ImmutableMap.of(MemoryModuleType.HOME, MemoryStatus.VALUE_PRESENT),
                        ImmutableList.of(
                              // Pair.of(SetClosestHomeAsWalkTarget.create(speedModifier), 1),
                              // Pair.of(InsideBrownianWalk.create(speedModifier), 4),
                              // Pair.of(GoToClosestVillage.create(speedModifier, 4), 2),
                              Pair.of(new DoNothing(20, 40), 2)))),
            getMinimalLookBehavior(),
            Pair.of(99, UpdateActivityFromSchedule.create()));
   }

   public static ImmutableList<Pair<Integer, ? extends BehaviorControl<? super CivilizedVillager>>> getCorePackage(
         float speedModifier) {
      return ImmutableList.of(
            Pair.of(0, new MoveToTargetSink()),
            Pair.of(0, new Swim(0.8F)),
            Pair.of(0, InteractWithDoor.create()),
            Pair.of(0, new LookAtTargetSink(45, 90)),
            Pair.of(0, new InvalidateImportantLocations()));
   }

   public static ImmutableList<Pair<Integer, ? extends BehaviorControl<? super CivilizedVillager>>> getIdlePackage(
         float speedModifier) {
      return ImmutableList.of(
            Pair.of(
                  3,
                  new RunOne(
                        ImmutableList.of(
                              Pair.of(
                                    InteractWith.of(
                                          EntityRegistry.CIVILIZED_VILLAGER.get(),
                                          8,
                                          MemoryModuleType.INTERACTION_TARGET,
                                          speedModifier,
                                          2),
                                    2),
                              Pair.of(
                                    InteractWith.of(
                                          EntityRegistry.CIVILIZED_VILLAGER.get(),
                                          8,
                                          AgeableMob::canBreed,
                                          AgeableMob::canBreed,
                                          MemoryModuleType.BREED_TARGET,
                                          speedModifier,
                                          2),
                                    1),
                              Pair.of(
                                    InteractWith
                                          .of(EntityType.CAT, 8, MemoryModuleType.INTERACTION_TARGET, speedModifier, 2),
                                    1),
                              Pair.of(VillageBoundRandomStroll.create(speedModifier), 1),
                              Pair.of(SetWalkTargetFromLookTarget.create(speedModifier, 2), 1),
                              // Pair.of(new JumpOnBed(speedModifier), 1),
                              Pair.of(new DoNothing(30, 60), 1)))),
            Pair.of(3, SetLookAndInteract.create(EntityType.PLAYER, 4)),
            // Pair.of(
            // 3,
            // new GateBehavior(
            // ImmutableMap.of(),
            // ImmutableSet.of(MemoryModuleType.INTERACTION_TARGET),
            // GateBehavior.OrderPolicy.ORDERED,
            // GateBehavior.RunningPolicy.RUN_ONE,
            // ImmutableList.of(Pair.of(new TradeWithVillager(), 1)))),
            // Pair.of(
            // 3,
            // new GateBehavior(
            // ImmutableMap.of(),
            // ImmutableSet.of(MemoryModuleType.BREED_TARGET),
            // GateBehavior.OrderPolicy.ORDERED,
            // GateBehavior.RunningPolicy.RUN_ONE,
            // ImmutableList.of(Pair.of(new VillagerMakeLove(), 1)))),
            getFullLookBehavior(),
            Pair.of(99, UpdateActivityFromSchedule.create()));
   }

   private static Pair<Integer, BehaviorControl<CivilizedVillager>> getMinimalLookBehavior() {
      return Pair.of(
            5,
            new RunOne(
                  ImmutableList.of(
                        Pair.of(SetEntityLookTarget.create(EntityRegistry.CIVILIZED_VILLAGER.get(), 8.0F), 2),
                        Pair.of(SetEntityLookTarget.create(EntityType.PLAYER, 8.0F), 2),
                        Pair.of(new DoNothing(30, 60), 8))));
   }

   private static Pair<Integer, BehaviorControl<CivilizedVillager>> getFullLookBehavior() {
      return Pair.of(
            5,
            new RunOne(
                  ImmutableList.of(
                        Pair.of(SetEntityLookTarget.create(EntityType.CAT, 8.0F), 8),
                        Pair.of(SetEntityLookTarget.create(EntityRegistry.CIVILIZED_VILLAGER.get(), 8.0F), 2),
                        Pair.of(SetEntityLookTarget.create(EntityType.VILLAGER, 8.0F), 2),
                        Pair.of(SetEntityLookTarget.create(EntityType.PLAYER, 8.0F), 2),
                        Pair.of(SetEntityLookTarget.create(MobCategory.CREATURE, 8.0F), 1),
                        Pair.of(SetEntityLookTarget.create(MobCategory.WATER_CREATURE, 8.0F), 1),
                        Pair.of(SetEntityLookTarget.create(MobCategory.AXOLOTLS, 8.0F), 1),
                        Pair.of(SetEntityLookTarget.create(MobCategory.UNDERGROUND_WATER_CREATURE, 8.0F), 1),
                        Pair.of(SetEntityLookTarget.create(MobCategory.WATER_AMBIENT, 8.0F), 1),
                        Pair.of(SetEntityLookTarget.create(MobCategory.MONSTER, 8.0F), 1),
                        Pair.of(new DoNothing(30, 60), 2))));
   }
}
