package com.uncreated.civilized.core.building.logistics.orders.task;

import java.util.Collection;
import java.util.function.Predicate;

import com.uncreated.civilized.core.building.logistics.AggregateItemStack;

import lombok.Getter;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;

public record PendingRequiredItems(Predicate<ItemStack> itemSearch, int requiredAmountToTake, boolean isStockSufficient,
      boolean willTake, boolean villagerHasRequiredItems, PendingRequiredItems.StockInfo stock) {

   @Getter
   public static class StockInfo {
      private final Collection<Container> sourceChests;
      private final AggregateItemStack sourceStock;

      public StockInfo(Collection<Container> sourceChests, AggregateItemStack sourceStock) {
         this.sourceChests = sourceChests;
         this.sourceStock = sourceStock;
      }
   }
}
