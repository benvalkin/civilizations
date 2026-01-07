package com.uncreated.civilized.core.building.crafting.orders;

import com.uncreated.civilized.core.building.logistics.AggregateItemStack;

import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.level.Level;

public class ProduceUpTo extends ProductionOrder {
   private final int max;

   public ProduceUpTo(Level level, String key, CraftingRecipe recipe, int max) {
      super(level, key, recipe);
      this.max = max;
   }

   @Override
   protected int calculateStockDeficit(AggregateItemStack stockChestsStock) {

      return max - stockChestsStock.getCount();
   }
}
