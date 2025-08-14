package com.uncreated.civilized.core.building.logistics.orders.imports;

import com.uncreated.civilized.core.building.logistics.AggregateItemStack;
import lombok.Getter;
import net.minecraft.world.item.ItemStack;

import java.util.function.Predicate;

@Getter
public class ImportWhenStockpilesLow extends ImportOrder {
   protected final int shipmentItemCount;
   protected final int destinationStockpileThreshold;

   public ImportWhenStockpilesLow(String key, Predicate<ItemStack> itemSearch, Origin origin, int shipmentItemCount, int destinationStockpileThreshold) {
      super(key, itemSearch, origin);
       this.shipmentItemCount = shipmentItemCount;
       this.destinationStockpileThreshold = destinationStockpileThreshold;
   }

   @Override
   protected boolean shouldShip(
           AggregateItemStack sourceStock,
           AggregateItemStack destinationStock) {
      return destinationStock.getCount() < destinationStockpileThreshold;
   }

   @Override
   protected int getItemCountForNextShipment(
           AggregateItemStack sourceStock,
           AggregateItemStack destinationStock) {
      return Math.clamp(shipmentItemCount, 0, 64);
   }
}
