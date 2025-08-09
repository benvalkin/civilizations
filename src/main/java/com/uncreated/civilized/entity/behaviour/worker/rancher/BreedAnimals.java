package com.uncreated.civilized.entity.behaviour.worker.rancher;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.slf4j.Logger;

import com.google.common.collect.ImmutableMap;
import com.mojang.logging.LogUtils;
import com.uncreated.civilized.core.building.Building;
import com.uncreated.civilized.core.building.ServerBuildingsStore;
import com.uncreated.civilized.entity.CivilizedVillager;
import com.uncreated.civilized.entity.behaviour.MediumDistanceTravelTask;
import com.uncreated.civilized.entity.behaviour.worker.WorkTaskBehaviour;
import com.uncreated.civilized.neoforge.registration.ai.AIRegistry;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;

public class BreedAnimals<T extends Animal> extends WorkTaskBehaviour {
   public static final Logger LOGGER = LogUtils.getLogger();
   private long lastWorkTime;
   private Building workSite;
   private MediumDistanceTravelTask travelHelper;

   private final Class<T> animalMobType;

   public BreedAnimals(Class<T> animalMobType) {
      super(
            ImmutableMap.of(
                  MemoryModuleType.LOOK_TARGET,
                  MemoryStatus.VALUE_ABSENT,
                  MemoryModuleType.WALK_TARGET,
                  MemoryStatus.VALUE_ABSENT,
                  MemoryModuleType.JOB_SITE,
                  MemoryStatus.VALUE_PRESENT,
                  AIRegistry.MM_HOLDING_WORK_INPUT_RESOURCES.get(),
                  MemoryStatus.VALUE_PRESENT));
      this.animalMobType = animalMobType;
   }

   @Override
   protected boolean checkExtraStartConditions(ServerLevel level, CivilizedVillager villager) {

      workSite = ServerBuildingsStore.INSTANCE.get(villager.getInfo().getPrimaryWorksiteId());

      List<Animal> breedableAnimals = getBreedableAnimals(level);
      if (breedableAnimals.size() < 2) {
         return false;
      }

      Stream<ItemStack> animalFoodItemsInventory = getAnimalFoodItemsInventory(villager, breedableAnimals.getFirst());
      if (animalFoodItemsInventory.mapToInt(ItemStack::getCount).sum() < 2) {
         villager.getBrain().eraseMemory(AIRegistry.MM_HOLDING_WORK_INPUT_RESOURCES.get());
         return false;
      }

      return true;
   }

   @Override
   protected void start(ServerLevel level, CivilizedVillager villager, long gameTime) {
      super.start(level, villager, gameTime);
      AABB tooCloseBounds = workSite.getBounds().getEncapsulatingAABB();
      AABB closeEnoughBounds = tooCloseBounds.inflate(4);

      travelHelper =
            new MediumDistanceTravelTask(
                  villager,
                  workSite.getBlockPos(),
                  (v, d, closEnough) -> closeEnoughBounds.contains(v.position()),
                  Math.max((int) closeEnoughBounds.getXsize() / 2, (int) closeEnoughBounds.getZsize() / 2));
   }

   @Override
   protected void stop(ServerLevel level, CivilizedVillager villager, long gameTime) {
      super.stop(level, villager, gameTime);
   }

   @Override
   protected boolean canStillUse(ServerLevel level, CivilizedVillager villager, long gameTime) {
      return villager.getBrain().checkMemory(MemoryModuleType.JOB_SITE, MemoryStatus.VALUE_PRESENT);
   }

   @Override
   protected void tick(ServerLevel level, CivilizedVillager villager, long gameTime) {

      if (!travelHelper.isJourneySuccessful()) {
         travelHelper.walkToPoi(gameTime);
         return;
      }

      if (gameTime - lastWorkTime > 20) {

         lastWorkTime = gameTime;

         Optional<Animal> animal = getBreedableAnimals(level).stream().findAny();
         if (animal.isEmpty()) {
            doStop(level, villager, gameTime);
            return;
         }

         Optional<ItemStack> animalFoodItemsInventory = getAnimalFoodItemsInventory(villager, animal.get()).findAny();
         if (animalFoodItemsInventory.isEmpty()) {
            doStop(level, villager, gameTime);
            return;
         }

         villager.swing(InteractionHand.MAIN_HAND, true);

         animal.get().setInLove(null);
         villager.getWorkInputInventory().removeItemType(animalFoodItemsInventory.get().getItem(), 1);
      }
   }

   protected List<Animal> getBreedableAnimals(ServerLevel level) {
      return level.getEntitiesOfClass(
            Animal.class,
            workSite.getBounds().getEncapsulatingAABB(),
            a -> !a.isBaby() && !a.isInLove() && a.canFallInLove() && a.getAge() == 0);
   }

   private Stream<ItemStack> getAnimalFoodItemsInventory(CivilizedVillager villager, Animal animal) {
      return villager.getWorkInputInventory().getItems().stream().filter(animal::isFood);
   }
}
