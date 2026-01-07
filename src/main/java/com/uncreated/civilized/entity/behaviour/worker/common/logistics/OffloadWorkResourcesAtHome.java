package com.uncreated.civilized.entity.behaviour.worker.common.logistics;

import java.util.Optional;
import java.util.Set;

import com.google.common.collect.ImmutableMap;
import com.uncreated.civilized.core.building.Building;
import com.uncreated.civilized.core.building.ServerBuildingsStore;
import com.uncreated.civilized.core.building.logistics.LogisticsManager;
import com.uncreated.civilized.core.building.logistics.orders.StorehouseOrder;
import com.uncreated.civilized.core.building.logistics.orders.exports.ExportAlways;
import com.uncreated.civilized.core.settlement.entity.LoadedSettlement;
import com.uncreated.civilized.core.settlement.entity.LoadedSettlements;
import com.uncreated.civilized.entity.CivilizedVillager;
import com.uncreated.civilized.neoforge.registration.ai.AIRegistry;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.item.Item;

public class OffloadWorkResourcesAtHome extends ExchangeResourcesAtBuilding {

   public OffloadWorkResourcesAtHome() {
      super(
            ImmutableMap.of(
                  MemoryModuleType.LOOK_TARGET,
                  MemoryStatus.VALUE_ABSENT,
                  MemoryModuleType.WALK_TARGET,
                  MemoryStatus.VALUE_ABSENT,
                  AIRegistry.MM_HAS_WORK_OUTPUT_RESOURCES.get(),
                  MemoryStatus.VALUE_PRESENT));
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
         ExportAlways exportOrder =
               new ExportAlways(level, item.toString(), i -> i.is(item), StorehouseOrder.Origin.AUTOMATIC);
         exportOrder.setExpiry(12000);
         logisticsManager.registerOrder(targetbuilding, exportOrder);
      });

      villager.getBrain().eraseMemory(AIRegistry.MM_HAS_WORK_OUTPUT_RESOURCES.get());
      villager.getBrain().setMemory(AIRegistry.MM_EXPORT_DESIRED.get(), true);
   }
}
