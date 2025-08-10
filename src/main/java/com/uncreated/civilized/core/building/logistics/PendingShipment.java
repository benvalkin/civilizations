package com.uncreated.civilized.core.building.logistics;

import lombok.Getter;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;

import java.util.Collection;
import java.util.function.Predicate;

@Getter
public class PendingShipment {
   private final Predicate<ItemStack> itemSearch;
   private final int amount;
   private final boolean shouldShip;
    private final StockInfo stock;


    public PendingShipment(Predicate<ItemStack> itemSearch, int amount, boolean shouldShip, StockInfo stock) {
      this.itemSearch = itemSearch;
      this.amount = amount;
      this.shouldShip = shouldShip;
        this.stock = stock;
    }

    @Getter
   public static class StockInfo {
      private final Collection<Container> destinationChests;
      private final Collection<Container> sourceChests;
      private final AggregateItemStack destinationStock;
      private final AggregateItemStack sourceStock;

       public StockInfo(Collection<Container> destinationChests, Collection<Container> sourceChests, AggregateItemStack destinationStock, AggregateItemStack sourceStock) {
           this.destinationChests = destinationChests;
           this.sourceChests = sourceChests;
           this.destinationStock = destinationStock;
           this.sourceStock = sourceStock;
       }
   }
}
