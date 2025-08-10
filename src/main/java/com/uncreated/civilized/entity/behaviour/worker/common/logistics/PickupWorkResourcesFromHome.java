package com.uncreated.civilized.entity.behaviour.worker.common.logistics;

import com.google.common.collect.ImmutableMap;
import com.uncreated.civilized.core.building.Building;
import com.uncreated.civilized.core.building.ServerBuildingsStore;
import com.uncreated.civilized.core.building.logistics.LogisticsManager;
import com.uncreated.civilized.core.building.logistics.orders.LogisticsOrder;
import com.uncreated.civilized.core.building.logistics.orders.imports.ImportUpTo;
import com.uncreated.civilized.core.building.logistics.orders.imports.ImportWhenStockpilesLow;
import com.uncreated.civilized.core.settlement.ServerSettlementsStore;
import com.uncreated.civilized.entity.CivilizedVillager;
import com.uncreated.civilized.neoforge.registration.ai.AIRegistry;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.Optional;
import java.util.function.Predicate;

public class PickupWorkResourcesFromHome extends ExchangeResourcesAtBuilding {

   private final Predicate<ItemStack> itemSearch;
    private final int amount;

    public PickupWorkResourcesFromHome(Predicate<ItemStack> itemSearch, int amount) {
      super(
              ImmutableMap.of(
                      MemoryModuleType.LOOK_TARGET,
                      MemoryStatus.VALUE_ABSENT,
                      MemoryModuleType.WALK_TARGET,
                      MemoryStatus.VALUE_ABSENT,
                      AIRegistry.MM_HOLDING_WORK_INPUT_RESOURCES.get(),
                      MemoryStatus.VALUE_ABSENT));
      this.itemSearch = itemSearch;
        this.amount = amount;
    }

   @Override
   protected boolean checkExtraStartConditions(ServerLevel level, CivilizedVillager villager) {

        if (!super.checkExtraStartConditions(level, villager))
            return false;

      return super.checkExtraStartConditions(level, villager) && targetBuildingChestsHaveResources(itemSearch);
   }

   @Override
   protected Optional<Building> findTargetBuilding(ServerLevel level, CivilizedVillager villager) {
      return ServerBuildingsStore.INSTANCE.find(villager.getInfo().getHomeBuildingId());
   }

   @Override
   protected void exchangeResources(ServerLevel level, CivilizedVillager villager, long tickTime) {

      int successfullyTransferred = fillInventoryFromChests(villager.getWorkInputInventory(), itemSearch, amount);

      if (successfullyTransferred > 0)
         villager.getBrain().setMemory(AIRegistry.MM_HOLDING_WORK_INPUT_RESOURCES.get(), true);
      else
         villager.getBrain().setMemory(AIRegistry.MM_HOLDING_WORK_INPUT_RESOURCES.get(), false);
   }
}
