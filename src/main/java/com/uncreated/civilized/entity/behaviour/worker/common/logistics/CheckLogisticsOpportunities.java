package com.uncreated.civilized.entity.behaviour.worker.common.logistics;

import java.util.List;
import java.util.Optional;

import com.google.common.collect.ImmutableMap;
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
import com.uncreated.civilized.entity.behaviour.worker.WorkTaskBehaviour;
import com.uncreated.civilized.neoforge.registration.ai.AIRegistry;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;

public class CheckLogisticsOpportunities extends WorkTaskBehaviour {

   private LoadedSettlement settlement;
   private Building home;
   private Building storehouse;

   public CheckLogisticsOpportunities() {
      super(
            ImmutableMap.of(
                  MemoryModuleType.LOOK_TARGET,
                  MemoryStatus.VALUE_ABSENT,
                  MemoryModuleType.WALK_TARGET,
                  MemoryStatus.VALUE_ABSENT,
                  AIRegistry.MM_EXPORT_DESIRED.get(),
                  MemoryStatus.VALUE_ABSENT,
                  AIRegistry.MM_IMPORT_DESIRED.get(),
                  MemoryStatus.VALUE_ABSENT,
                  AIRegistry.MM_BUSY_OFFLOADING_EXPORTS.get(),
                  MemoryStatus.VALUE_ABSENT,
                  AIRegistry.MM_BUSY_OFFLOADING_IMPORTS.get(),
                  MemoryStatus.VALUE_ABSENT));
   }

   long timeLastChecked = 0;

   @Override
   protected boolean checkExtraStartConditions(ServerLevel level, CivilizedVillager villager) {

      if (!super.checkExtraStartConditions(level, villager))
         return false;

      Optional<LoadedSettlement> loadedSettlement = LoadedSettlements.checkLoaded(villager.getInfo().getSettlementId());
      if (loadedSettlement.isEmpty())
         return false;

      settlement = loadedSettlement.get();

      home = ServerBuildingsStore.INSTANCE.find(villager.getInfo().getHomeBuildingId()).orElse(null);
      if (home == null)
         return false;

      storehouse = ServerBuildingsStore.INSTANCE.findStorehouse(villager.getInfo().getSettlementId()).orElse(null);
      if (storehouse == null)
         return false;

      return true;
   }

   @Override
   protected boolean canStillUse(ServerLevel level, CivilizedVillager entity, long gameTime) {
      return false; // stop immediately (once-off behaviour)
   }

   @Override
   protected void start(ServerLevel level, CivilizedVillager villager, long gameTime) {

      if (gameTime - timeLastChecked < 20 * 30)
         return;

      timeLastChecked = gameTime;

      List<Container> homeChests = LogisticsOrder.findChests(level, home);
      List<Container> storehouseChests = LogisticsOrder.findChests(level, storehouse);

      if (!checkForExportOrders(homeChests, storehouseChests, villager))
         checkForImportOrders(storehouseChests, homeChests, villager);

      villager.getBrain().setMemory(AIRegistry.MM_HAS_NON_IDLE_WORK_TASK.get(), true);
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
            villager.getBrain().setMemory(AIRegistry.MM_EXPORT_DESIRED.get(), true);
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
            villager.getBrain().setMemory(AIRegistry.MM_IMPORT_DESIRED.get(), true);
            return true;
         }
      }

      return false;
   }
}
