package com.uncreated.civilized.entity.behaviour.worker.common.logistics;

import java.util.Optional;
import java.util.Set;

import com.uncreated.civilized.core.building.Building;
import com.uncreated.civilized.core.building.ServerBuildingsStore;
import com.uncreated.civilized.core.building.logistics.LogisticsManager;
import com.uncreated.civilized.core.building.logistics.orders.StorehouseOrder;
import com.uncreated.civilized.core.building.logistics.orders.exports.ExportEverything;
import com.uncreated.civilized.core.settlement.entity.LoadedSettlement;
import com.uncreated.civilized.core.settlement.entity.LoadedSettlements;
import com.uncreated.civilized.entity.CivilizedVillager;
import com.uncreated.civilized.entity.behaviour.worker.WorkStates;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;

public class DropoffWorkOutputAtHome extends ExchangeResourcesAtBuilding {

   public DropoffWorkOutputAtHome() {
      super(WorkStates.DROPPING_OFF_WORK_OUTPUT_AT_HOME, 120 * 20, 30 * 20);
   }

   @Override
   protected Optional<Building> findTargetBuilding(ServerLevel level, CivilizedVillager villager) {
      return ServerBuildingsStore.INSTANCE.find(villager.getInfo().getHomeBuildingId());
   }

   @Override
   protected void exchangeResources(ServerLevel level, CivilizedVillager villager, long tickTime) {

      dumpInventoryToChests(villager.getWorkInputInventory());

      Set<Item> toExport = dumpInventoryToChests(villager.getWorkOutputInventory());
      Optional<LoadedSettlement> loadedSettlement = LoadedSettlements.checkLoaded(villager.getInfo().getSettlementId());
      if (loadedSettlement.isEmpty()) {
         doStop(level, villager, tickTime);
         return;
      }

      LogisticsManager logisticsManager = loadedSettlement.get().getBehaviour().getLogisticsManager();

      toExport.forEach(item -> {
         ExportEverything exportOrder =
               new ExportEverything(level, item.toString(), i -> i.is(item), StorehouseOrder.Origin.AUTOMATIC);
         exportOrder.setExpiry(12000);
         logisticsManager.registerOrder(targetbuilding, exportOrder);
      });
   }
}
