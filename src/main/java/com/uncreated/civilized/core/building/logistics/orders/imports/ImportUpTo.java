package com.uncreated.civilized.core.building.logistics.orders.imports;

import java.util.function.Predicate;

import com.uncreated.civilized.core.building.logistics.AggregateItemStack;

import lombok.Getter;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

@Getter
public class ImportUpTo extends ImportOrder {
   protected final int shipmentItemCount;
   protected final int max;

   public ImportUpTo(
         Level level,
         String key,
         Predicate<ItemStack> itemSearch,
         Origin origin,
         int shipmentItemCount,
         int max) {
      super(level, key, itemSearch, origin);
      this.shipmentItemCount = shipmentItemCount;
      this.max = max;
   }

   @Override
   protected boolean shouldShip(AggregateItemStack sourceStock, AggregateItemStack destinationStock) {
      return destinationStock.getCount() < max;
   }

   @Override
   protected int getItemCountForNextShipment(AggregateItemStack sourceStock, AggregateItemStack destinationStock) {
      return Math.clamp(shipmentItemCount, 0, 64);
   }
}
