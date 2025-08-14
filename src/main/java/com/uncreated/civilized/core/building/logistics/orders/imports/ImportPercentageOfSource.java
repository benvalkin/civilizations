package com.uncreated.civilized.core.building.logistics.orders.imports;

import com.uncreated.civilized.core.building.logistics.AggregateItemStack;

import lombok.Getter;
import net.minecraft.world.item.ItemStack;

import java.util.function.Predicate;

@Getter
public class ImportPercentageOfSource extends ImportOrder {

   private final float requiredPercentageOfSource;

   public ImportPercentageOfSource(String key, Predicate<ItemStack> itemSearch, Origin origin, float requiredPercentageOfSource) {
      super(key, itemSearch, origin);
       this.requiredPercentageOfSource = requiredPercentageOfSource;
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
      long requiredDestinationStock = Math.round(requiredPercentageOfSource * sourceStock.getCount());

      return Math.clamp(requiredDestinationStock - destinationStock.getCount(), 0, 64);
   }
}
