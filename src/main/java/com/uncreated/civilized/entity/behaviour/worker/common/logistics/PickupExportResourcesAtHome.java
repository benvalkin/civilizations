package com.uncreated.civilized.entity.behaviour.worker.common.logistics;

import com.google.common.collect.ImmutableMap;
import com.uncreated.civilized.core.building.Building;
import com.uncreated.civilized.core.building.ServerBuildingsStore;
import com.uncreated.civilized.core.building.logistics.PendingShipment;
import com.uncreated.civilized.core.building.logistics.orders.LogisticsOrder;
import com.uncreated.civilized.core.building.logistics.orders.LogisticsOrders;
import com.uncreated.civilized.core.settlement.ServerSettlementsStore;
import com.uncreated.civilized.core.settlement.Settlement;
import com.uncreated.civilized.entity.CivilizedVillager;
import com.uncreated.civilized.neoforge.registration.ai.AIRegistry;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;

import java.util.Collection;
import java.util.Optional;

public class PickupExportResourcesAtHome extends ExchangeResourcesAtBuilding {

   private Building storehouse;
   private Settlement settlement;
   private Collection<PendingShipment> pendingShipments;

   public PickupExportResourcesAtHome() {
      super(
            ImmutableMap.of(
                  MemoryModuleType.LOOK_TARGET,
                  MemoryStatus.VALUE_ABSENT,
                  MemoryModuleType.WALK_TARGET,
                  MemoryStatus.VALUE_ABSENT,
                  AIRegistry.MM_LOGISTICS_RUN_INTERVAL.get(),
                  MemoryStatus.VALUE_ABSENT,
                  AIRegistry.MM_HOLDING_EXPORT_RESOURCES.get(),
                  MemoryStatus.VALUE_ABSENT
                 ));
   }

   @Override
   protected Optional<Building> findTargetBuilding(ServerLevel level, CivilizedVillager villager) {
      return ServerBuildingsStore.INSTANCE.find(villager.getInfo().getHomeBuildingId());
   }

   @Override
   protected boolean checkExtraStartConditions(ServerLevel level, CivilizedVillager villager) {

      if (!super.checkExtraStartConditions(level, villager))
         return false;

      storehouse = ServerBuildingsStore.INSTANCE.findStorehouse(villager.getInfo().getSettlementId()).orElse(null);
      if (storehouse == null)
         return false;

      settlement = ServerSettlementsStore.INSTANCE.get(villager.getInfo().getSettlementId());
      return true;
   }

   @Override
   protected void exchangeResources(ServerLevel level, CivilizedVillager villager, long tickTime) {
      boolean holdingExportResources = false;

      dumpInventoryToChests(villager.getWorkInputInventory());
      dumpInventoryToChests(villager.getWorkOutputInventory());
      dumpInventoryToChests(villager.getLogisticsInventory());

      for (LogisticsOrder order : settlement.getLogisticsManager().getExportOrders(targetBuilding).orders()) {

         PendingShipment shipment = order.getNextShipment(targetBuilding, storehouse, level);
         if (order.takeShipment(villager, shipment))
            holdingExportResources = true;
      }

      // add back any items that are also mandated by import orders
      for (LogisticsOrder order : settlement.getLogisticsManager().getImportOrders(targetBuilding).orders()) {
         PendingShipment shipment = order.getNextShipment(storehouse, targetBuilding, level);
         order.returnShipment(villager, shipment);
      }

      villager.getBrain().setMemory(AIRegistry.MM_HOLDING_EXPORT_RESOURCES.get(), holdingExportResources);
      villager.getBrain().setMemoryWithExpiry(AIRegistry.MM_LOGISTICS_RUN_INTERVAL.get(), true, 20 * 60 * 3);
   }
}
