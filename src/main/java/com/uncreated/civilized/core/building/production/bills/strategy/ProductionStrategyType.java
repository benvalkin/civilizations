package com.uncreated.civilized.core.building.production.bills.strategy;

import com.uncreated.civilized.core.building.production.bills.ProductionBill;

import net.minecraft.network.chat.Component;

public enum ProductionStrategyType {
   PRODUCE_INFINITE, PRODUCE_UP_TO;

   public boolean requiresAmount() {
      return switch (this) {
         case PRODUCE_INFINITE -> false;
         case PRODUCE_UP_TO -> true;
      };
   }

   public Component getSimpleDescription() {
      return Component.translatable("production_bill.production_strategy.description.simple." + this.name().toLowerCase());
   }

   public static Component getComplexDescription(ProductionBill bill) {
      return switch (bill.getProductionStrategy().getType()) {
      case PRODUCE_INFINITE ->
         Component.translatable("production_bill.production_strategy.description.complex.produce_infinite");
      case PRODUCE_UP_TO -> Component.translatable(
            "production_bill.production_strategy.description.complex.produce_up_to",
            bill.getBillAmount());
      };
   }
}
