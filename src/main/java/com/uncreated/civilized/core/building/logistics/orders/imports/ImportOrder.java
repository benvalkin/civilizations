package com.uncreated.civilized.core.building.logistics.orders.imports;

import java.util.function.Predicate;

import com.uncreated.civilized.core.building.logistics.orders.StorehouseOrder;

import lombok.Getter;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public abstract class ImportOrder extends StorehouseOrder {

   private int minShipmentSize;
   private int maxShipmentSize;

   public ImportOrder(
         Level level,
         String key,
         Predicate<ItemStack> itemSearch,
         Origin origin,
         int minShipmentSize,
         int maxShipmentSize) {
      super(level, key, itemSearch, origin, minShipmentSize, maxShipmentSize);
   }
}
