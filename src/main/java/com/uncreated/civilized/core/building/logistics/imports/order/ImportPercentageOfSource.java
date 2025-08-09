package com.uncreated.civilized.core.building.logistics.imports.order;

import com.uncreated.civilized.core.building.logistics.AggregateItemStack;
import com.uncreated.civilized.core.building.logistics.imports.ImportOrder;

import lombok.Getter;
import net.minecraft.world.item.ItemStack;

@Getter
public class ImportPercentageOfSource extends ImportOrder {

   private final float requiredPercentageOfSource;

   public ImportPercentageOfSource(String key, ItemStack itemType, float requiredPercentageOfSource) {
      super(key, itemType);
      this.requiredPercentageOfSource = requiredPercentageOfSource;
   }

   @Override
   protected boolean shouldImport(AggregateItemStack exportSourceStock, AggregateItemStack importDestinationStock) {
      return false;
   }

   @Override
   protected int getItemCountForNextShipment(
         AggregateItemStack exportSourceStock,
         AggregateItemStack importDestinationStock) {
      long requiredPercentageOfSourceRealised = Math.round(requiredPercentageOfSource * exportSourceStock.getCount());

      return Math.clamp(requiredPercentageOfSourceRealised - importDestinationStock.getCount(), 0, 64);
   }
}
