package com.uncreated.civilized.core.building.logistics.orders.imports;

import com.uncreated.civilized.core.building.logistics.AggregateItemStack;
import com.uncreated.civilized.core.building.logistics.orders.ImportOrder;
import com.uncreated.civilized.core.building.logistics.orders.LogisticsOrder;

import lombok.Getter;
import net.minecraft.world.item.ItemStack;

import java.util.function.Predicate;

@Getter
public class ImportUpTo extends ImportOrder {
   protected final int amount;

   public ImportUpTo(String key, Predicate<ItemStack> itemSearch, Origin origin, int amount) {
      super(key, itemSearch, origin);
       this.amount = amount;
   }

   @Override
   protected boolean shouldShip(
           AggregateItemStack sourceStock,
           AggregateItemStack destinationStock) {
      return destinationStock.getCount() < amount;
   }

   @Override
   protected int getItemCountForNextShipment(
           AggregateItemStack sourceStock,
           AggregateItemStack destinationStock) {
      return Math.clamp(amount - destinationStock.getCount(), 0, 64);
   }
}
