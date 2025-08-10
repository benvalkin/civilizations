package com.uncreated.civilized.entity.behaviour.worker.common.logistics;

import com.mojang.logging.LogUtils;
import com.uncreated.civilized.core.building.Building;
import com.uncreated.civilized.entity.CivilizedVillager;
import com.uncreated.civilized.entity.behaviour.MediumDistanceTravelTask;
import com.uncreated.civilized.entity.behaviour.worker.WorkTaskBehaviour;
import com.uncreated.civilized.util.ContainerHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import org.slf4j.Logger;

import java.util.*;
import java.util.function.Predicate;

public abstract class ExchangeResourcesAtBuilding extends WorkTaskBehaviour {

   private static final Logger LOGGER = LogUtils.getLogger();

   protected Building targetBuilding;
   private List<ChestBlockEntity> chestsAtTarget;
   private MediumDistanceTravelTask travelHelper;

   public ExchangeResourcesAtBuilding(Map<MemoryModuleType<?>, MemoryStatus> entryCondition) {
      super(entryCondition);
   }

   protected abstract Optional<Building> findTargetBuilding(ServerLevel level, CivilizedVillager villager);

   @Override
   protected boolean checkExtraStartConditions(ServerLevel level, CivilizedVillager villager) {

      Optional<Building> targetBuilding = findTargetBuilding(level, villager);
      if (targetBuilding.isEmpty())
         return false;

      this.targetBuilding = targetBuilding.get();

      chestsAtTarget =
              this.targetBuilding
                  .getBounds()
                  .getBlockEntitiesInsideBuilding(level)
                  .stream()
                  .filter(b -> b instanceof ChestBlockEntity)
                  .map(b -> (ChestBlockEntity) b)
                  .toList();

      if (chestsAtTarget.isEmpty())
         return false;

      return true;
   }

   @Override
   protected void start(ServerLevel level, CivilizedVillager villager, long gameTime) {
      super.start(level, villager, gameTime);
      LOGGER.info("Villager going to offload resources at {}.", targetBuilding.toStringLite());

      travelHelper =
            new MediumDistanceTravelTask(
                  villager,
                  targetBuilding.getBlockPos(), 3);
   }

   @Override
   protected void stop(ServerLevel level, CivilizedVillager villager, long gameTime) {
      super.stop(level, villager, gameTime);
      LOGGER.info("Villager finished offloading resources at {}.", targetBuilding.toStringLite());
   }

   @Override
   protected void tick(ServerLevel level, CivilizedVillager villager, long tickTime) {

      if (!travelHelper.isJourneySuccessful()) {
         travelHelper.walkToPoi(tickTime);
         return;
      }

      exchangeResources(level, villager, tickTime);

      doStop(level, villager, tickTime);
   }

   protected abstract void exchangeResources(ServerLevel level, CivilizedVillager villager, long tickTime);

   protected Set<Item> dumpInventoryToChests(Container villagerInventory) {

      Set<Item> itemTypesDumped = new HashSet<>();

      for (int i = 0; i < villagerInventory.getContainerSize(); i++) {

         ItemStack item = villagerInventory.getItem(i);
         if (item.isEmpty())
            continue;

         Item itemType = item.getItem();

         for (var chest : chestsAtTarget) {
            // try to add item to chest
            ItemStack remainder = ContainerHelper.addItemNicely(chest, item);
            villagerInventory.removeItem(i, item.getCount() - remainder.getCount());

            // if there is no remainder, we successfully inserted the stack
            if (remainder.isEmpty()) {
               itemTypesDumped.add(itemType);
               break;
            }
         }
      }

      return itemTypesDumped;
   }

   protected int fillInventoryFromChests(Container villagerInventory, Predicate<ItemStack> itemSearch, int quota) {

      int transferRemaining = quota;
      for (var chest : chestsAtTarget) {

         int transferred =
                 ContainerHelper.transferNicely(
                         chest,
                         villagerInventory,
                         itemSearch,
                         transferRemaining);

         transferRemaining -= transferred;

         if (transferRemaining <= 0)
            break;
      }

      return quota - transferRemaining;
   }

   protected boolean targetBuildingChestsHaveResources(Predicate<ItemStack> itemSearch) {
      for (var chest : chestsAtTarget) {
         if (chest.hasAnyMatching(itemSearch))
            return true;
      }
      return false;
   }
}
