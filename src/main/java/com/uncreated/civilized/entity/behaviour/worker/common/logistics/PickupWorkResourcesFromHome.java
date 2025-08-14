package com.uncreated.civilized.entity.behaviour.worker.common.logistics;

import com.google.common.collect.ImmutableMap;
import com.uncreated.civilized.core.building.Building;
import com.uncreated.civilized.core.building.ServerBuildingsStore;
import com.uncreated.civilized.core.building.logistics.LogisticsManager;
import com.uncreated.civilized.core.building.logistics.orders.LogisticsOrders;
import com.uncreated.civilized.core.building.logistics.orders.task.PendingRequiredItems;
import com.uncreated.civilized.core.building.logistics.orders.task.TaskItemRequirement;
import com.uncreated.civilized.core.settlement.ServerSettlementsStore;
import com.uncreated.civilized.entity.CivilizedVillager;
import com.uncreated.civilized.neoforge.registration.ai.AIRegistry;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;

import java.util.Optional;

public class PickupWorkResourcesFromHome extends ExchangeResourcesAtBuilding {

    public PickupWorkResourcesFromHome() {
      super(
              ImmutableMap.of(
                      MemoryModuleType.LOOK_TARGET,
                      MemoryStatus.VALUE_ABSENT,
                      MemoryModuleType.WALK_TARGET,
                      MemoryStatus.VALUE_ABSENT,
                      AIRegistry.MM_HOLDING_WORK_INPUT_RESOURCES.get(),
                      MemoryStatus.VALUE_ABSENT));
    }

   @Override
   protected Optional<Building> findTargetBuilding(ServerLevel level, CivilizedVillager villager) {
      return ServerBuildingsStore.INSTANCE.find(villager.getInfo().getHomeBuildingId());
   }

   @Override
   protected void exchangeResources(ServerLevel level, CivilizedVillager villager, long tickTime) {

        dumpInventoryToChests(villager.getWorkInputInventory());
        dumpInventoryToChests(villager.getWorkOutputInventory());

       LogisticsManager logisticsManager = ServerSettlementsStore.INSTANCE.get(villager.getInfo().getSettlementId()).getLogisticsManager();
       LogisticsOrders<TaskItemRequirement> taskItemRequirements = logisticsManager.getTaskItemRequirements(targetBuilding);

       boolean holdingWorkItems = false;
       for (TaskItemRequirement requirement : taskItemRequirements.orders()) {

           PendingRequiredItems requiredItems = requirement.getRequiredItemsToTake(targetBuilding, villager, level);
           if (!requiredItems.shouldTake() || requiredItems.villagerHasRequiredItems())
               continue;

           if (requirement.takeRequiredItems(villager, requiredItems))
               holdingWorkItems = true;
       }

      if (holdingWorkItems)
         villager.getBrain().setMemory(AIRegistry.MM_HOLDING_WORK_INPUT_RESOURCES.get(), true);
      else
         villager.getBrain().setMemory(AIRegistry.MM_HOLDING_WORK_INPUT_RESOURCES.get(), false);
   }
}
