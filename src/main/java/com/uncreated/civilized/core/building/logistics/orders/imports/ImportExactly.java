package com.uncreated.civilized.core.building.logistics.orders.imports;

import java.util.function.Predicate;

import com.uncreated.civilized.core.building.logistics.AggregateItemStack;

import lombok.Getter;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

@Getter
public class ImportExactly extends ImportOrder {
   protected final int shipmentItemCount;

   public ImportExactly(
         Level level,
         String key,
         Predicate<ItemStack> itemSearch,
         Origin origin,
         int shipmentItemCount) {
      super(level, key, itemSearch, origin);
      this.shipmentItemCount = shipmentItemCount;
   }

   @Override
   protected boolean shouldShip(AggregateItemStack sourceStock, AggregateItemStack destinationStock) {
      return true;
   }

   @Override
   protected int getItemCountForNextShipment(AggregateItemStack sourceStock, AggregateItemStack destinationStock) {
      return Math.clamp(shipmentItemCount, 0, sourceStock.getCount());
   }
}
