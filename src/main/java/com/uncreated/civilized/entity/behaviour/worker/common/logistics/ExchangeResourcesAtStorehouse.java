package com.uncreated.civilized.entity.behaviour.worker.common.logistics;

import java.util.Optional;

import com.uncreated.civilized.core.building.logistics.PendingShipment;
import com.uncreated.civilized.core.building.logistics.orders.LogisticsOrder;
import com.uncreated.civilized.core.building.logistics.orders.LogisticsOrders;
import com.uncreated.civilized.core.settlement.ServerSettlementsStore;
import com.uncreated.civilized.core.settlement.Settlement;
import org.slf4j.Logger;

import com.google.common.collect.ImmutableMap;
import com.mojang.logging.LogUtils;
import com.uncreated.civilized.core.building.Building;
import com.uncreated.civilized.core.building.ServerBuildingsStore;
import com.uncreated.civilized.entity.CivilizedVillager;
import com.uncreated.civilized.neoforge.registration.ai.AIRegistry;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;

public class ExchangeResourcesAtStorehouse extends ExchangeResourcesAtBuilding {

   private static final Logger LOGGER = LogUtils.getLogger();

   private Building home;
   private Settlement settlement;

   public ExchangeResourcesAtStorehouse() {
      super(
            ImmutableMap.of(
                  MemoryModuleType.LOOK_TARGET,
                  MemoryStatus.VALUE_ABSENT,
                  MemoryModuleType.WALK_TARGET,
                  MemoryStatus.VALUE_ABSENT,
                  AIRegistry.MM_HOLDING_EXPORT_RESOURCES.get(),
                  MemoryStatus.VALUE_PRESENT,
                  AIRegistry.MM_HOLDING_IMPORT_RESOURCES.get(),
                  MemoryStatus.VALUE_ABSENT
                    ));
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

      settlement = ServerSettlementsStore.INSTANCE.get(villager.getInfo().getSettlementId());
      return true;
   }

   @Override
   protected void exchangeResources(ServerLevel level, CivilizedVillager villager, long tickTime) {

      dumpInventoryToChests(villager.getLogisticsInventory());
      villager.getBrain().eraseMemory(AIRegistry.MM_HOLDING_EXPORT_RESOURCES.get());

      LogisticsOrders importOrders = settlement.getLogisticsManager().getImportOrders(home);
      boolean holdingImportResources = false;
      for (LogisticsOrder order : importOrders.orders()) {

         PendingShipment shipment = order.getNextShipment(targetBuilding, home, level);
         if (!shipment.isShouldShip())
            continue;

         if (order.takeShipment(villager, shipment))
            holdingImportResources = true;
      }

      if (holdingImportResources)
         villager.getBrain().setMemory(AIRegistry.MM_HOLDING_IMPORT_RESOURCES.get(), true);
   }
}
