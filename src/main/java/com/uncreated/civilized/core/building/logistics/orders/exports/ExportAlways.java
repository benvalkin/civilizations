package com.uncreated.civilized.core.building.logistics.orders.exports;

import java.util.function.Predicate;

import com.uncreated.civilized.core.building.logistics.AggregateItemStack;

import lombok.Getter;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

@Getter
public class ExportAlways extends ExportOrder {

   public ExportAlways(Level level, String key, Predicate<ItemStack> itemSearch, Origin origin) {
      super(level, key, itemSearch, origin);
   }

   @Override
   protected boolean shouldShip(AggregateItemStack sourceStock, AggregateItemStack destinationStock) {
      return true;
   }

   @Override
   protected int getItemCountForNextShipment(AggregateItemStack sourceStock, AggregateItemStack destinationStock) {
      return 64;
   }
}
