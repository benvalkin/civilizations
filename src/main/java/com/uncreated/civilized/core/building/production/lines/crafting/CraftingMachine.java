package com.uncreated.civilized.core.building.production.lines.crafting;

import com.uncreated.civilized.core.building.production.RecipeProductionMachine;
import com.uncreated.civilized.core.building.production.bills.ProductionBill;
import com.uncreated.civilized.core.building.production.bills.ProductionType;

import net.minecraft.server.level.ServerLevel;

public class CraftingMachine extends RecipeProductionMachine<CraftingOrder> {

   @Override
   public CraftingOrder createOrder(String key, ProductionBill bill, ServerLevel serverLevel) {
      return new CraftingOrder(key, bill, serverLevel);
   }

   @Override
   public ProductionType getProductionType() {
      return ProductionType.CRAFTING;
   }
}
