package com.uncreated.civilized.core.building.crafting.bills.strategy;

import com.uncreated.civilized.core.building.crafting.bills.ProductionBill;

import net.minecraft.network.chat.Component;

public enum ProductionStrategyType {
   PRODUCE_INFINITE, PRODUCE_UP_TO;

   public String translationKey() {
      return "production_bill.production_strategy." + this.name().toLowerCase();
   }

   public static Component getBillStrategyDescription(ProductionBill bill) {
      return switch (bill.getProductionStrategy().getType()) {
      case PRODUCE_INFINITE ->
         Component.translatable("production_bill.description.production_strategy.produce_infinite");
      case PRODUCE_UP_TO ->
         Component.translatable("production_bill.description.production_strategy.produce_up_to", bill.getBillAmount());
      };
   }
}
