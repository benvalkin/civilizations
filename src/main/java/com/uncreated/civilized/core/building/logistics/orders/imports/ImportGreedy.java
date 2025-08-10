package com.uncreated.civilized.core.building.logistics.orders.imports;

import com.uncreated.civilized.core.building.logistics.AggregateItemStack;
import com.uncreated.civilized.core.building.logistics.orders.LogisticsOrder;

import lombok.Getter;
import net.minecraft.world.item.ItemStack;

import java.util.function.Predicate;

@Getter
public class ImportGreedy extends LogisticsOrder {

   public ImportGreedy(String key, Predicate<ItemStack> itemSearch, Origin origin) {
      super(key, itemSearch, origin);
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
      return 64;
   }
}
