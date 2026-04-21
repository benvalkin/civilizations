package com.uncreated.civilized.entity.behaviour.worker.common.logistics;

import java.util.List;
import java.util.Optional;

import com.uncreated.civilized.core.building.Building;
import com.uncreated.civilized.core.building.ServerBuildingsStore;
import com.uncreated.civilized.core.building.logistics.LogisticsManager;
import com.uncreated.civilized.core.building.logistics.PendingShipment;
import com.uncreated.civilized.core.building.logistics.orders.LogisticsOrder;
import com.uncreated.civilized.core.building.logistics.orders.LogisticsOrders;
import com.uncreated.civilized.core.building.logistics.orders.exports.ExportOrder;
import com.uncreated.civilized.core.building.logistics.orders.imports.ImportOrder;
import com.uncreated.civilized.core.settlement.entity.LoadedSettlement;
import com.uncreated.civilized.core.settlement.entity.LoadedSettlements;
import com.uncreated.civilized.entity.CivilizedVillager;
import com.uncreated.civilized.entity.behaviour.Cooldowns;
import com.uncreated.civilized.entity.behaviour.worker.WorkStates;
import com.uncreated.civilized.entity.behaviour.worker.WorkTaskBehaviour;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;

public class CheckLogisticsOpportunities extends WorkTaskBehaviour {

   private LoadedSettlement settlement;
   private Building home;
   private Building storehouse;

   public CheckLogisticsOpportunities() {
      super(WorkStates.CHECK_LOGISTICS_OPPORTUNITIES, 0, 5 * 20);
   }

   @Override
   protected boolean canStillUse(ServerLevel level, CivilizedVillager entity, long gameTime) {
      return false; // stop immediately (once-off behaviour)
   }

   @Override
   protected void start(ServerLevel level, CivilizedVillager villager, long gameTime) {

      Optional<LoadedSettlement> loadedSettlement = LoadedSettlements.checkLoaded(villager.getInfo().getSettlementId());
      if (loadedSettlement.isEmpty())
         return;

      settlement = loadedSettlement.get();

      home = ServerBuildingsStore.INSTANCE.find(villager.getInfo().getHomeBuildingId()).orElse(null);
      if (home == null)
         return;

      storehouse = ServerBuildingsStore.INSTANCE.findStorehouse(villager.getInfo().getSettlementId()).orElse(null);
      if (storehouse == null)
         return;

      List<Container> homeChests = LogisticsOrder.findChests(level, home);
      List<Container> storehouseChests = LogisticsOrder.findChests(level, storehouse);

      if (!getSharedCooldowns().hasCooldown(Cooldowns.EXPORT_RUN, level.getGameTime()) && checkForExportOrders(homeChests, storehouseChests, villager)) {
         getStateMachine().queueActionOnce(WorkStates.FETCHING_EXPORTS_FROM_HOME);
      } else if (!getSharedCooldowns().hasCooldown(Cooldowns.IMPORT_RUN, level.getGameTime()) && checkForImportOrders(storehouseChests, homeChests, villager)) {
         getStateMachine().queueActionOnce(WorkStates.FETCHING_IMPORTS_FROM_STOREHOUSE);
      }
   }

   private boolean checkForExportOrders(
         List<Container> source,
         List<Container> destination,
         CivilizedVillager villager) {

      LogisticsManager logisticsManager = settlement.getBehaviour().getLogisticsManager();
      LogisticsOrders<ExportOrder> exportOrders = logisticsManager.getExportOrders(home);

      for (ExportOrder order : exportOrders.orders()) {

         PendingShipment shipment = order.getNextShipment(source, destination);
         if (shipment.shouldShip()) {
            getStateMachine().queueActionOnce(WorkStates.FETCHING_EXPORTS_FROM_HOME);
            return true;
         }
      }

      return false;
   }

   private boolean checkForImportOrders(
         List<Container> source,
         List<Container> destination,
         CivilizedVillager villager) {

      LogisticsManager logisticsManager = settlement.getBehaviour().getLogisticsManager();
      LogisticsOrders<ImportOrder> importOrders = logisticsManager.getImportOrders(home);

      for (ImportOrder order : importOrders.orders()) {

         PendingShipment shipment = order.getNextShipment(source, destination);
         if (shipment.shouldShip()) {
            getStateMachine().queueActionOnce(WorkStates.FETCHING_IMPORTS_FROM_STOREHOUSE);
            return true;
         }
      }

      return false;
   }
}
