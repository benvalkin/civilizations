package com.uncreated.civilized.core.building.logistics.orders.task;

import java.util.function.Predicate;

import com.uncreated.civilized.core.building.logistics.AggregateItemStack;
import com.uncreated.civilized.util.ContainerHelper;

import lombok.Getter;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

@Getter
public class TaskConsumableItemRequirement extends TaskItemRequirement {
   private final int minAmount;
   private final int maxAmount;

   public TaskConsumableItemRequirement(
         Level level,
         String taskName,
         Predicate<ItemStack> itemSearch,
         Origin origin,
         int maxAmount) {
      this(level, taskName, itemSearch, origin, 1, maxAmount);
   }

   public TaskConsumableItemRequirement(
         Level level,
         String taskName,
         Predicate<ItemStack> itemSearch,
         Origin origin,
         int minAmount,
         int maxAmount) {
      super(level, taskName, itemSearch, origin);
      this.minAmount = minAmount;
      this.maxAmount = maxAmount;
   }

   @Override
   protected boolean canTake(AggregateItemStack sourceStock) {
      return sourceStock.getCount() >= minAmount;
   }

   @Override
   protected boolean villagerHasRequiredItems(Container villagerInventory) {
      return ContainerHelper.countItems(villagerInventory, itemSearch).getCount() >= minAmount;
   }

   @Override
   protected int getItemCountRequiredForTask(AggregateItemStack sourceStock) {
      return Math.clamp(maxAmount, 0, 64);
   }
}
