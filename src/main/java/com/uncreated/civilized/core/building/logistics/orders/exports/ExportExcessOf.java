package com.uncreated.civilized.core.building.logistics.orders.exports;

import com.uncreated.civilized.core.building.logistics.AggregateItemStack;
import lombok.Getter;
import net.minecraft.world.item.ItemStack;

import java.util.function.Predicate;

@Getter
public class ExportExcessOf extends ExportOrder {

   protected final int amount;

   public ExportExcessOf(String key, Predicate<ItemStack> itemSearch, Origin origin, int amount) {
      super(key, itemSearch, origin);
       this.amount = amount;
   }

   @Override
   protected boolean shouldShip(
           AggregateItemStack sourceStock,
           AggregateItemStack destinationStock) {
      return sourceStock.getCount() > amount;
   }

   @Override
   protected int getItemCountForNextShipment(
           AggregateItemStack sourceStock,
           AggregateItemStack destinationStock) {
      return Math.clamp(sourceStock.getCount() - amount, 0, 64);
   }
}
