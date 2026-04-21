package com.uncreated.civilized.core.building.production.bills.strategy;

import com.uncreated.civilized.core.building.logistics.AggregateItemStack;

public class ProduceUpTo implements IProductionStrategy {

   private final int max;

   public ProduceUpTo(int max) {
      this.max = max;
   }

   @Override
   public int calculateStockDeficit(AggregateItemStack stockChestsStock) {
      int diff = Math.clamp(max - stockChestsStock.getCount(), 0, max);
      return Math.min(diff, max);
   }

   @Override
   public ProductionStrategyType getType() {
      return ProductionStrategyType.PRODUCE_UP_TO;
   }
}
