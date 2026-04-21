package com.uncreated.civilized.core.building.production.lines.singleitem.cooking;

import com.uncreated.civilized.core.building.production.RecipeProductionMachine;
import com.uncreated.civilized.core.building.production.bills.ProductionBill;
import com.uncreated.civilized.core.building.production.lines.singleitem.SingleItemRecipeOrder;

import net.minecraft.server.level.ServerLevel;

public abstract class CookingMachine extends RecipeProductionMachine<SingleItemRecipeOrder> {

   @Override
   public SingleItemRecipeOrder createOrderFromBill(String key, ProductionBill bill, ServerLevel serverLevel) {
      return new SingleItemRecipeOrder(key, bill, serverLevel);
   }
}
