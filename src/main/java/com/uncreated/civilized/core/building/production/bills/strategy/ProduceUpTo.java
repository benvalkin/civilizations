package com.uncreated.civilized.core.building.production.bills.strategy;

import com.uncreated.civilized.core.building.logistics.AggregateItemStack;

public class ProduceUpTo implements IProductionStrategy {

   private final int max;

   public ProduceUpTo(int max) {
      this.max = max;
   }

   @Override
   public int calculateStockDeficit(AggregateItemStack stockChestsStock) {
      return max - stockChestsStock.getCount();
   }

   @Override
   public ProductionStrategyType getType() {
      return ProductionStrategyType.PRODUCE_UP_TO;
   }
}
