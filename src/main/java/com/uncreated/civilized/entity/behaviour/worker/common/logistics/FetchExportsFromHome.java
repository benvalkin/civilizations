package com.uncreated.civilized.entity.behaviour.worker.common.logistics;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.uncreated.civilized.core.building.Building;
import com.uncreated.civilized.core.building.ServerBuildingsStore;
import com.uncreated.civilized.core.building.logistics.LogisticsManager;
import com.uncreated.civilized.core.building.logistics.PendingShipment;
import com.uncreated.civilized.core.building.logistics.orders.LogisticsOrder;
import com.uncreated.civilized.core.building.logistics.orders.LogisticsOrders;
import com.uncreated.civilized.core.building.logistics.orders.StorehouseOrder;
import com.uncreated.civilized.core.building.logistics.orders.exports.ExportEverything;
import com.uncreated.civilized.core.building.logistics.orders.exports.ExportOrder;
import com.uncreated.civilized.core.building.logistics.orders.task.PendingRequiredItems;
import com.uncreated.civilized.core.building.logistics.orders.task.TaskItemRequirement;
import com.uncreated.civilized.core.settlement.entity.LoadedSettlement;
import com.uncreated.civilized.core.settlement.entity.LoadedSettlements;
import com.uncreated.civilized.entity.CivilizedVillager;
import com.uncreated.civilized.entity.behaviour.worker.WorkStates;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;

public class FetchExportsFromHome extends ExchangeResourcesAtBuilding {

   private Building storehouse;
   private LoadedSettlement settlement;
   private Collection<PendingShipment> pendingShipments;

   public FetchExportsFromHome() {
      super(WorkStates.FETCHING_EXPORTS_FROM_HOME, 120 * 20, 30 * 20);
   }

   @Override
   protected Optional<Building> findTargetBuilding(ServerLevel level, CivilizedVillager villager) {
      return ServerBuildingsStore.INSTANCE.find(villager.getInfo().getHomeBuildingId());
   }

   @Override
   protected boolean checkExtraStartConditions(ServerLevel level, CivilizedVillager villager) {

      if (!super.checkExtraStartConditions(level, villager))
         return false;

      Optional<LoadedSettlement> loadedSettlement = LoadedSettlements.checkLoaded(villager.getInfo().getSettlementId());
      if (loadedSettlement.isEmpty())
         return false;

      storehouse = ServerBuildingsStore.INSTANCE.findStorehouse(villager.getInfo().getSettlementId()).orElse(null);
      if (storehouse == null)
         return false;

      settlement = loadedSettlement.get();

      if (!areThereItemsToExport(settlement))
         return false;

      return true;
   }

   @Override
   protected void exchangeResources(ServerLevel level, CivilizedVillager villager, long tickTime) {
      boolean holdingExportResources = false;

      dumpInventoryToChests(villager.getWorkInputInventory());
      dumpInventoryToChests(villager.getLogisticsInventory());
      Set<Item> toExport = dumpInventoryToChests(villager.getWorkOutputInventory());

      LogisticsManager logisticsManager = settlement.getBehaviour().getLogisticsManager();

      toExport.forEach(item -> {
         ExportEverything exportOrder =
               new ExportEverything(level, item.toString(), i -> i.is(item), StorehouseOrder.Origin.AUTOMATIC);
         exportOrder.setExpiry(12000);
         logisticsManager.registerOrder(targetbuilding, exportOrder);
      });

      List<Container> source = LogisticsOrder.findChests(level, targetbuilding);
      List<Container> destination = LogisticsOrder.findChests(level, storehouse);
      for (StorehouseOrder order : logisticsManager.getExportOrders(targetbuilding).orders()) {

         PendingShipment shipment = order.getNextShipment(source, destination);
         if (order.takeShipment(villager, shipment))
            holdingExportResources = true;
      }

      // add back any items that are also mandated by import orders and task requirements
      for (StorehouseOrder order : logisticsManager.getImportOrders(targetbuilding).orders()) {
         PendingShipment shipment = order.getNextShipment(destination, source); // note: param inversion is correct here
         order.returnShipment(villager, shipment);
      }
      for (TaskItemRequirement requirement : logisticsManager.getTaskItemRequirements(targetbuilding).orders()) {
         PendingRequiredItems shipment = requirement.getRequiredItemsToTake(targetbuilding, villager, level);
         requirement.returnItems(villager, shipment);
      }

      if (holdingExportResources)
         getStateMachine().queueActionOnce(WorkStates.DROPPING_OFF_EXPORTS_AT_STOREHOUSE);
   }

   private boolean areThereItemsToExport(LoadedSettlement settlement) {

      List<Container> source = LogisticsOrder.findChests(settlement.getLevel(), targetbuilding);
      List<Container> destination = LogisticsOrder.findChests(settlement.getLevel(), storehouse);

      LogisticsManager logisticsManager = settlement.getBehaviour().getLogisticsManager();
      LogisticsOrders<ExportOrder> exportOrders = logisticsManager.getExportOrders(targetbuilding);

      for (ExportOrder order : exportOrders.orders()) {

         PendingShipment shipment = order.getNextShipment(source, destination);
         if (shipment.shouldShip()) {
            return true;
         }
      }

      return false;
   }
}
