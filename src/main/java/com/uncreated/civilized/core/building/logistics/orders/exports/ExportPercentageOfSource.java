package com.uncreated.civilized.core.building.logistics.orders.exports;

import java.util.function.Predicate;

import com.uncreated.civilized.core.building.logistics.AggregateItemStack;

import lombok.Getter;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

@Getter
public class ExportPercentageOfSource extends ExportOrder {

   private final float requiredPercentageAtDestination;

   public ExportPercentageOfSource(
         Level level,
         String key,
         Predicate<ItemStack> itemSearch,
         Origin origin,
         float requiredPercentageAtDestination) {
      super(level, key, itemSearch, origin);
      this.requiredPercentageAtDestination = requiredPercentageAtDestination;
   }

   @Override
   protected boolean shouldShip(AggregateItemStack sourceStock, AggregateItemStack destinationStock) {
      return true;
   }

   @Override
   protected int getItemCountForNextShipment(AggregateItemStack sourceStock, AggregateItemStack destinationStock) {

      long requiredDestinationStock = Math.round(requiredPercentageAtDestination * sourceStock.getCount());

      return Math.clamp(requiredDestinationStock - destinationStock.getCount(), 0, 64);
   }
}
