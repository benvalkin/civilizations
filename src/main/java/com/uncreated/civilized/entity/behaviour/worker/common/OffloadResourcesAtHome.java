package com.uncreated.civilized.entity.behaviour.worker.common;

import java.util.List;
import java.util.Optional;

import com.uncreated.civilized.entity.behaviour.MediumDistanceTravelTask;
import org.slf4j.Logger;

import com.google.common.collect.ImmutableMap;
import com.mojang.logging.LogUtils;
import com.uncreated.civilized.core.building.Building;
import com.uncreated.civilized.core.building.ServerBuildingsStore;
import com.uncreated.civilized.entity.CivilizedVillager;
import com.uncreated.civilized.entity.behaviour.worker.WorkTaskBehaviour;
import com.uncreated.civilized.neoforge.registration.ai.AIRegistry;
import com.uncreated.civilized.util.ContainerHelper;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.ChestBlockEntity;

public class OffloadResourcesAtHome extends WorkTaskBehaviour {

   private static final Logger LOGGER = LogUtils.getLogger();

   private Building storehouse;
   private Building home;
   private List<ChestBlockEntity> chestsAtHome;
   private MediumDistanceTravelTask travelHelper;

   public OffloadResourcesAtHome() {
      super(
            ImmutableMap.of(
                  MemoryModuleType.LOOK_TARGET,
                  MemoryStatus.VALUE_ABSENT,
                  MemoryModuleType.WALK_TARGET,
                  MemoryStatus.VALUE_ABSENT,
                  AIRegistry.MM_HOLDING_WORK_OUTPUT_RESOURCES.get(),
                  MemoryStatus.VALUE_PRESENT));
   }

   @Override
   protected boolean checkExtraStartConditions(ServerLevel level, CivilizedVillager villager) {

      Optional<Building> home = ServerBuildingsStore.INSTANCE.find(villager.getInfo().getHomeBuildingId());
      if (home.isEmpty()) {
         return false;
      }

      List<ChestBlockEntity> chestAtHome =
            home.get()
                  .getBounds()
                  .getBlockEntitiesInsideBuilding(level)
                  .stream()
                  .filter(b -> b instanceof ChestBlockEntity)
                  .map(b -> (ChestBlockEntity) b)
                  .toList();

      if (chestAtHome.isEmpty()) {
         return false;
      }

      this.home = home.get();
      this.chestsAtHome = chestAtHome;
      return true;
   }

   @Override
   protected void start(ServerLevel level, CivilizedVillager villager, long gameTime) {
      super.start(level, villager, gameTime);
      LOGGER.info("Villager going to offload resources.");
      offloaded = false;

      travelHelper =
            new MediumDistanceTravelTask(
                  villager,
                  villager.getBrain().getMemory(MemoryModuleType.HOME).orElseThrow().pos(), 3);
   }

   @Override
   protected void stop(ServerLevel level, CivilizedVillager villager, long gameTime) {
      super.stop(level, villager, gameTime);
      LOGGER.info("Villager stopped offloading resources.");
   }

   @Override
   protected boolean canStillUse(ServerLevel level, CivilizedVillager entity, long gameTime) {
      return !offloaded;
   }

   private boolean offloaded = false;

   @Override
   protected void tick(ServerLevel level, CivilizedVillager villager, long tickTime) {

      if (!travelHelper.isJourneySuccessful()) {
         travelHelper.walkToPoi(tickTime);
         return;
      }

      transferInventoryToChests(villager.getWorkOutputInventory());
      transferInventoryToChests(villager.getWorkInputInventory());

      offloaded = true;
      villager.getBrain().eraseMemory(AIRegistry.MM_HOLDING_WORK_OUTPUT_RESOURCES.get());
      villager.getBrain().eraseMemory(AIRegistry.MM_HOLDING_WORK_INPUT_RESOURCES.get());
   }

   private void transferInventoryToChests(Container inventory) {
      for (int i = 0; i < inventory.getContainerSize(); i++) {

         ItemStack item = inventory.getItem(i);
         if (item.isEmpty())
            continue;

         for (var chest : chestsAtHome) {
            // try to add item to chest
            ItemStack remainder = ContainerHelper.addItemNicely(chest, item);
            inventory.removeItem(i, item.getCount() - remainder.getCount());

            // if there is no remainder, we successfully inserted the stack
            if (remainder.isEmpty())
               break;
         }
      }
   }
}
