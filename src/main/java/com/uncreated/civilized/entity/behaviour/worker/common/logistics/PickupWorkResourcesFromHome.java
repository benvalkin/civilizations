package com.uncreated.civilized.entity.behaviour.worker.common.logistics;

import java.util.Optional;

import com.google.common.collect.ImmutableMap;
import com.uncreated.civilized.core.building.Building;
import com.uncreated.civilized.core.building.ServerBuildingsStore;
import com.uncreated.civilized.core.building.logistics.LogisticsManager;
import com.uncreated.civilized.core.building.logistics.orders.LogisticsOrders;
import com.uncreated.civilized.core.building.logistics.orders.task.PendingRequiredItems;
import com.uncreated.civilized.core.building.logistics.orders.task.TaskItemRequirement;
import com.uncreated.civilized.core.settlement.entity.LoadedSettlement;
import com.uncreated.civilized.core.settlement.entity.LoadedSettlements;
import com.uncreated.civilized.entity.CivilizedVillager;
import com.uncreated.civilized.neoforge.registration.ai.AIRegistry;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;

public class PickupWorkResourcesFromHome extends ExchangeResourcesAtBuilding {

   private LoadedSettlement settlement;

   public PickupWorkResourcesFromHome() {
      super(
            ImmutableMap.of(
                  MemoryModuleType.LOOK_TARGET,
                  MemoryStatus.VALUE_ABSENT,
                  MemoryModuleType.WALK_TARGET,
                  MemoryStatus.VALUE_ABSENT,
                  AIRegistry.MM_HAS_WORK_INPUT_RESOURCES.get(),
                  MemoryStatus.VALUE_ABSENT));
   }

   @Override
   protected boolean checkExtraStartConditions(ServerLevel level, CivilizedVillager villager) {

      if (!super.checkExtraStartConditions(level, villager))
         return false;

      Optional<LoadedSettlement> loadedSettlement = LoadedSettlements.checkLoaded(villager.getInfo().getSettlementId());
      if (loadedSettlement.isEmpty())
         return false;

      settlement = loadedSettlement.get();
      return true;
   }

   @Override
   protected Optional<Building> findTargetBuilding(ServerLevel level, CivilizedVillager villager) {
      return ServerBuildingsStore.INSTANCE.find(villager.getInfo().getHomeBuildingId());
   }

   @Override
   protected void exchangeResources(ServerLevel level, CivilizedVillager villager, long tickTime) {

      dumpInventoryToChests(villager.getWorkInputInventory());
      dumpInventoryToChests(villager.getWorkOutputInventory());

      LogisticsManager logisticsManager = settlement.getBehaviour().getLogisticsManager();
      LogisticsOrders<TaskItemRequirement> taskItemRequirements =
            logisticsManager.getTaskItemRequirements(targetbuilding);

      boolean holdingWorkItems = false;
      for (TaskItemRequirement requirement : taskItemRequirements.orders()) {

         PendingRequiredItems requiredItems = requirement.getRequiredItemsToTake(targetbuilding, villager, level);
         if (!requiredItems.shouldTake() || requiredItems.villagerHasRequiredItems())
            continue;

         if (requirement.takeRequiredItems(villager, requiredItems))
            holdingWorkItems = true;
      }

      if (holdingWorkItems)
         villager.getBrain().setMemory(AIRegistry.MM_HAS_WORK_INPUT_RESOURCES.get(), true);
      else
         villager.getBrain().setMemory(AIRegistry.MM_HAS_WORK_INPUT_RESOURCES.get(), false);
   }
}
