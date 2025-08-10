package com.uncreated.civilized.core.building.logistics.orders.exports;

import com.uncreated.civilized.core.building.logistics.AggregateItemStack;
import com.uncreated.civilized.core.building.logistics.orders.ExportOrder;
import com.uncreated.civilized.core.building.logistics.orders.LogisticsOrder;
import lombok.Getter;
import net.minecraft.world.item.ItemStack;

import java.util.function.Predicate;

@Getter
public class ExportPercentageOfSource extends ExportOrder {

   private final float requiredPercentageAtDestination;

   public ExportPercentageOfSource(String key, Predicate<ItemStack> itemSearch, Origin origin, float requiredPercentageAtDestination) {
      super(key, itemSearch, origin);
       this.requiredPercentageAtDestination = requiredPercentageAtDestination;
   }

   @Override
   protected boolean shouldShip(
           AggregateItemStack sourceStock,
           AggregateItemStack destinationStock) {
      return true;
   }

   @Override
   protected int getItemCountForNextShipment(
           AggregateItemStack sourceStock,
           AggregateItemStack destinationStock) {

      long requiredDestinationStock = Math.round(requiredPercentageAtDestination * sourceStock.getCount());

      return Math.clamp(requiredDestinationStock - destinationStock.getCount(), 0, 64);
   }
}
