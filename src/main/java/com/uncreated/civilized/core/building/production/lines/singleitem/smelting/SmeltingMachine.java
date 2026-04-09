package com.uncreated.civilized.core.building.production.lines.singleitem.smelting;

import com.uncreated.civilized.core.building.production.RecipeProductionMachine;
import com.uncreated.civilized.core.building.production.bills.ProductionBill;
import com.uncreated.civilized.core.building.production.bills.ProductionType;
import com.uncreated.civilized.core.building.production.lines.singleitem.SingleItemRecipeOrder;

import net.minecraft.server.level.ServerLevel;

public class SmeltingMachine extends RecipeProductionMachine<SingleItemRecipeOrder> {

   @Override
   public SingleItemRecipeOrder createOrder(String key, ProductionBill bill, ServerLevel serverLevel) {
      return new SingleItemRecipeOrder(key, bill, serverLevel);
   }

   @Override
   public ProductionType getProductionType() {
      return ProductionType.SMELTING;
   }
}
