package com.uncreated.civilized.entity.behaviour.worker.common.logistics;

import java.util.Optional;

import com.uncreated.civilized.core.building.Building;
import com.uncreated.civilized.core.building.ServerBuildingsStore;
import com.uncreated.civilized.core.building.logistics.LogisticsManager;
import com.uncreated.civilized.core.building.logistics.orders.LogisticsOrders;
import com.uncreated.civilized.core.building.logistics.orders.task.PendingRequiredItems;
import com.uncreated.civilized.core.building.logistics.orders.task.TaskItemRequirement;
import com.uncreated.civilized.core.settlement.entity.LoadedSettlement;
import com.uncreated.civilized.core.settlement.entity.LoadedSettlements;
import com.uncreated.civilized.entity.CivilizedVillager;
import com.uncreated.civilized.entity.behaviour.worker.WorkStates;

import net.minecraft.server.level.ServerLevel;

public class FetchWorkInputFromHome extends ExchangeResourcesAtBuilding {

   private LoadedSettlement settlement;

   public FetchWorkInputFromHome() {
      super(WorkStates.FETCHING_WORK_INPUT_FROM_HOME, 120 * 20, 30 * 20);
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

      for (TaskItemRequirement requirement : taskItemRequirements.orders()) {

         PendingRequiredItems requiredItems = requirement.getRequiredItemsToTake(targetbuilding, villager, level);

         if (!requiredItems.isStockSufficient()) {
            getStateMachine().queueActionOnce(WorkStates.FETCHING_IMPORTS_FROM_STOREHOUSE);
            continue;
         }

         if (!requiredItems.willTake())
            continue;

         requirement.takeRequiredItems(villager, requiredItems);
      }
   }
}
