package com.uncreated.civilized.core.building.production.lines.singleitem;

import com.uncreated.civilized.core.building.production.bills.ProductionBill;
import com.uncreated.civilized.core.building.production.orders.ProductionOrder;
import com.uncreated.civilized.core.building.production.orders.recipe.RecipeAssembler;

import net.minecraft.core.RegistryAccess;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.crafting.Recipe;

public class SingleItemRecipeOrder extends ProductionOrder {
   public SingleItemRecipeOrder(String key, ProductionBill bill, ServerLevel level) {
      super(key, bill, level);
   }

   @Override
   protected RecipeAssembler<?, ?> getRecipeAssembler(Recipe<?> value, RegistryAccess registryAccess) {
      return new SingleItemRecipeAssembler(value, registryAccess);
   }
}
