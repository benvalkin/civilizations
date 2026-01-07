package com.uncreated.civilized.core.building.crafting.orders;

import com.uncreated.civilized.core.building.logistics.AggregateItemStack;

import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.level.Level;

public class ProduceInfinite extends ProductionOrder {
   private final int batchSize;

   public ProduceInfinite(Level level, String key, CraftingRecipe recipe, int batchSize) {
      super(level, key, recipe);
      this.batchSize = batchSize;
   }

   @Override
   protected int calculateStockDeficit(AggregateItemStack stock) {
      return this.batchSize;
   }
}
