package com.uncreated.civilized.core.building.logistics.orders.imports;

import java.util.function.Predicate;

import com.uncreated.civilized.core.building.logistics.AggregateItemStack;

import lombok.Getter;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

@Getter
public class ImportUpTo extends ImportOrder {
   protected final int upTo;

   public ImportUpTo(
         Level level,
         String key,
         Predicate<ItemStack> itemSearch,
         Origin origin,
         int minShipmentSize,
         int maxShipmentSize,
         int upTo) {
      super(level, key, itemSearch, origin, minShipmentSize, maxShipmentSize);
      this.upTo = upTo;
   }

   @Override
   protected int getDeficitAtDestination(AggregateItemStack sourceStock, AggregateItemStack destinationStock) {
      return upTo - destinationStock.getCount();
   }
}
