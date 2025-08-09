package com.uncreated.civilized.core.building.logistics.imports.order;

import com.uncreated.civilized.core.building.logistics.AggregateItemStack;
import com.uncreated.civilized.core.building.logistics.imports.ImportOrder;

import lombok.Getter;
import net.minecraft.world.item.ItemStack;

@Getter
public class ImportUpTo extends ImportOrder {
   protected final int amount;

   public ImportUpTo(String key, ItemStack itemType, int amount) {
      super(key, itemType);
      this.amount = amount;
   }

   @Override
   protected boolean shouldImport(AggregateItemStack exportSourceStock, AggregateItemStack importDestinationStock) {
      return importDestinationStock.getCount() < amount;
   }

   @Override
   protected int getItemCountForNextShipment(
         AggregateItemStack exportSourceStock,
         AggregateItemStack importDestinationStock) {
      return Math.clamp(amount - importDestinationStock.getCount(), 0, 64);
   }
}
