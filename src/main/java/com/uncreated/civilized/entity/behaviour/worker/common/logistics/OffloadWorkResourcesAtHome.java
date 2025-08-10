package com.uncreated.civilized.entity.behaviour.worker.common.logistics;

import java.util.Optional;
import java.util.Set;

import com.google.common.collect.ImmutableMap;
import com.uncreated.civilized.core.building.Building;
import com.uncreated.civilized.core.building.ServerBuildingsStore;
import com.uncreated.civilized.core.building.logistics.LogisticsManager;
import com.uncreated.civilized.core.building.logistics.orders.ExportOrder;
import com.uncreated.civilized.core.building.logistics.orders.LogisticsOrder;
import com.uncreated.civilized.core.building.logistics.orders.exports.ExportAll;
import com.uncreated.civilized.core.building.logistics.orders.imports.ImportWhenStockpilesLow;
import com.uncreated.civilized.core.settlement.ServerSettlementsStore;
import com.uncreated.civilized.entity.CivilizedVillager;
import com.uncreated.civilized.neoforge.registration.ai.AIRegistry;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class OffloadWorkResourcesAtHome extends ExchangeResourcesAtBuilding {

   public OffloadWorkResourcesAtHome() {
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
   protected Optional<Building> findTargetBuilding(ServerLevel level, CivilizedVillager villager) {
      return ServerBuildingsStore.INSTANCE.find(villager.getInfo().getHomeBuildingId());
   }

   @Override
   protected void exchangeResources(ServerLevel level, CivilizedVillager villager, long tickTime) {

      dumpInventoryToChests(villager.getWorkInputInventory());
      Set<Item> toExport = dumpInventoryToChests(villager.getWorkOutputInventory());

      LogisticsManager logisticsManager = ServerSettlementsStore.INSTANCE.get(villager.getInfo().getSettlementId()).getLogisticsManager();
      toExport.forEach(item -> {
         ExportAll exportOrder = new ExportAll(item.toString(), i -> i.is(item), LogisticsOrder.Origin.AUTOMATIC);
         logisticsManager.registerOrder(targetBuilding, exportOrder, 10);
      });

      villager.getBrain().eraseMemory(AIRegistry.MM_HOLDING_WORK_OUTPUT_RESOURCES.get());
      villager.getBrain().eraseMemory(AIRegistry.MM_HOLDING_WORK_INPUT_RESOURCES.get());
   }
}
