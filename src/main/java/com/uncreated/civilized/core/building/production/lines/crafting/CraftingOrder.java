package com.uncreated.civilized.core.building.production.lines.crafting;

import com.uncreated.civilized.core.building.production.bills.ProductionBill;
import com.uncreated.civilized.core.building.production.orders.ProductionOrder;
import com.uncreated.civilized.core.building.production.orders.recipe.RecipeAssembler;

import net.minecraft.core.RegistryAccess;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.crafting.Recipe;

public class CraftingOrder extends ProductionOrder {
   public CraftingOrder(String key, ProductionBill bill, ServerLevel level) {
      super(key, bill, level);
   }

   @Override
   protected RecipeAssembler<?, ?> getRecipeAssembler(Recipe<?> value, RegistryAccess registryAccess) {
      return new CraftingRecipeAssembler(value, registryAccess);
   }
}
