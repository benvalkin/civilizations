package com.uncreated.civilized.entity.behaviour.worker.common.logistics;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import com.uncreated.civilized.core.building.Building;
import com.uncreated.civilized.core.building.ServerBuildingsStore;
import com.uncreated.civilized.core.building.logistics.LogisticsManager;
import com.uncreated.civilized.core.building.logistics.PendingShipment;
import com.uncreated.civilized.core.building.logistics.orders.LogisticsOrder;
import com.uncreated.civilized.core.building.logistics.orders.imports.ImportOrder;
import com.uncreated.civilized.core.settlement.entity.LoadedSettlement;
import com.uncreated.civilized.core.settlement.entity.LoadedSettlements;
import com.uncreated.civilized.entity.CivilizedVillager;
import com.uncreated.civilized.entity.behaviour.worker.WorkStates;
import com.uncreated.civilized.util.ContainerHelper;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class FetchExportsFromHome extends ExchangeResourcesAtBuilding {

   private Building storehouse;
   private LoadedSettlement settlement;

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

      if (!areThereItemsToExport(settlement, targetbuilding, storehouse))
         return false;

      return true;
   }

   @Override
   protected void exchangeResources(ServerLevel level, CivilizedVillager villager, long tickTime) {

      dumpInventoryToChests(villager.getWorkInputInventory());
      dumpInventoryToChests(villager.getLogisticsInventory());
      dumpInventoryToChests(villager.getWorkOutputInventory());

      if (takeEverythingButLeaveImportOrders(villager, level))
         getStateMachine().queueActionOnce(WorkStates.DROPPING_OFF_EXPORTS_AT_STOREHOUSE);
   }

   public boolean takeEverythingButLeaveImportOrders(CivilizedVillager villager, Level level) {

      LogisticsManager logisticsManager = settlement.getBehaviour().getLogisticsManager();

      List<Container> buildingContainers = LogisticsOrder.findChests(level, targetbuilding);
      List<Container> storehouseContainers = LogisticsOrder.findChests(level, storehouse);

      Collection<ImportOrder> importOrders = logisticsManager.getImportOrders(targetbuilding).orders();

      boolean holdingExportItems = false;
      for (Container buildingContainer : buildingContainers) {

         for (int i = 0; i < buildingContainer.getContainerSize(); i++) {
            ItemStack item = buildingContainer.getItem(i);

            if (item.isEmpty())
               continue;

            int toTake =
                  adjustTakeSizeIfMandatedByImportOrder(item, buildingContainers, storehouseContainers, importOrders);

            ItemStack remainder =
                  ContainerHelper.addItemNicely(villager.getLogisticsInventory(), item.copyWithCount(toTake));
            item.shrink(toTake);
            ContainerHelper.addItemNicely(buildingContainer, remainder);
            buildingContainer.setItem(i, item);
            holdingExportItems = true;
         }
      }

      return holdingExportItems;
   }

   private static int adjustTakeSizeIfMandatedByImportOrder(
         ItemStack item,
         List<Container> buildingContainers,
         List<Container> storehouseContainers,
         Collection<ImportOrder> storehouseOrders) {
      int toSendBack = 0;
      boolean mandatedByImportOrder = false;
      for (ImportOrder order : storehouseOrders) {
         if (!order.getItemSearch().test(item))
            continue;

         mandatedByImportOrder = true;
         // this item is madated by an import order
         PendingShipment nextShipment = order.getNextShipment(storehouseContainers, buildingContainers);
         if (nextShipment.stockSurplusAtDestination() <= 0)
            continue; // if building does not have a surplus of this item, we cannot export it back to the
         // storehouse

         int extra = Math.min(nextShipment.stockSurplusAtDestination(), item.getCount());
         if (extra > toSendBack)
            toSendBack = extra;
      }

      if (mandatedByImportOrder)
         return toSendBack;
      else
         return item.getCount();
   }

   public static boolean areThereItemsToExport(LoadedSettlement settlement, Building targetbuilding, Building storehouse) {

      List<Container> buildingContainers = LogisticsOrder.findChests(settlement.getLevel(), targetbuilding);
      List<Container> storehouseContainers = LogisticsOrder.findChests(settlement.getLevel(), storehouse);

      LogisticsManager logisticsManager = settlement.getBehaviour().getLogisticsManager();
      Collection<ImportOrder> importOrders = logisticsManager.getImportOrders(targetbuilding).orders();

      for (Container buildingContainer : buildingContainers) {

         for (int i = 0; i < buildingContainer.getContainerSize(); i++) {
            ItemStack item = buildingContainer.getItem(i);

            if (item.isEmpty())
               continue;

            int toTake = adjustTakeSizeIfMandatedByImportOrder(item, buildingContainers, storehouseContainers, importOrders);

            if (toTake > 0)
               return true;
         }
      }

      return false;
   }
}
