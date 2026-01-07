package com.uncreated.civilized.entity.behaviour.worker.common.logistics;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;

import com.google.common.collect.ImmutableMap;
import com.mojang.logging.LogUtils;
import com.uncreated.civilized.core.building.Building;
import com.uncreated.civilized.core.building.ServerBuildingsStore;
import com.uncreated.civilized.core.building.logistics.LogisticsManager;
import com.uncreated.civilized.core.building.logistics.PendingShipment;
import com.uncreated.civilized.core.building.logistics.orders.LogisticsOrder;
import com.uncreated.civilized.core.building.logistics.orders.LogisticsOrders;
import com.uncreated.civilized.core.building.logistics.orders.imports.ImportOrder;
import com.uncreated.civilized.core.settlement.entity.LoadedSettlement;
import com.uncreated.civilized.core.settlement.entity.LoadedSettlements;
import com.uncreated.civilized.entity.CivilizedVillager;
import com.uncreated.civilized.neoforge.registration.ai.AIRegistry;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;

public class PickupImportsAtStorehouse extends ExchangeResourcesAtBuilding {

   private static final Logger LOGGER = LogUtils.getLogger();

   private Building home;
   private LoadedSettlement settlement;

   public PickupImportsAtStorehouse() {
      super(
            ImmutableMap.of(
                  MemoryModuleType.LOOK_TARGET,
                  MemoryStatus.VALUE_ABSENT,
                  MemoryModuleType.WALK_TARGET,
                  MemoryStatus.VALUE_ABSENT,
                  AIRegistry.MM_IMPORT_DESIRED.get(),
                  MemoryStatus.VALUE_PRESENT));
   }

   @Override
   protected Optional<Building> findTargetBuilding(ServerLevel level, CivilizedVillager villager) {
      return ServerBuildingsStore.INSTANCE.findStorehouse(villager.getInfo().getSettlementId());
   }

   @Override
   protected boolean checkExtraStartConditions(ServerLevel level, CivilizedVillager villager) {

      if (!super.checkExtraStartConditions(level, villager))
         return false;

      home = ServerBuildingsStore.INSTANCE.find(villager.getInfo().getHomeBuildingId()).orElse(null);
      if (home == null)
         return false;

      Optional<LoadedSettlement> loadedSettlement = LoadedSettlements.checkLoaded(villager.getInfo().getSettlementId());
      if (loadedSettlement.isEmpty())
         return false;

      settlement = loadedSettlement.get();

      if (!areThereItemsToImport(settlement))
         return false;

      return true;
   }

   @Override
   protected void exchangeResources(ServerLevel level, CivilizedVillager villager, long tickTime) {

      villager.getBrain().eraseMemory(AIRegistry.MM_IMPORT_DESIRED.get());

      LogisticsOrders<ImportOrder> importOrders = settlement.getBehaviour().getLogisticsManager().getImportOrders(home);
      boolean areThereItemsToImport = false;

      List<Container> source = LogisticsOrder.findChests(level, targetbuilding);
      List<Container> destination = LogisticsOrder.findChests(level, home);
      for (ImportOrder order : importOrders.orders()) {

         PendingShipment shipment = order.getNextShipment(source, destination);
         if (!shipment.shouldShip())
            continue;

         if (order.takeShipment(villager, shipment))
            areThereItemsToImport = true;
      }

      if (areThereItemsToImport)
         villager.getBrain().setMemory(AIRegistry.MM_BUSY_OFFLOADING_IMPORTS.get(), true);
   }

   private boolean areThereItemsToImport(LoadedSettlement settlement) {

      List<Container> source = LogisticsOrder.findChests(settlement.getLevel(), targetbuilding);
      List<Container> destination = LogisticsOrder.findChests(settlement.getLevel(), home);

      LogisticsManager logisticsManager = settlement.getBehaviour().getLogisticsManager();
      LogisticsOrders<ImportOrder> importOrders = logisticsManager.getImportOrders(home);

      for (ImportOrder order : importOrders.orders()) {

         PendingShipment shipment = order.getNextShipment(source, destination);
         if (shipment.shouldShip()) {
            return true;
         }
      }

      return false;
   }
}
