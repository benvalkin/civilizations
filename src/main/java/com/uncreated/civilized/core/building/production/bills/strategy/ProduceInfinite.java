package com.uncreated.civilized.core.building.production.bills.strategy;

import com.uncreated.civilized.core.building.logistics.AggregateItemStack;

public class ProduceInfinite implements IProductionStrategy {

    private final int batchSize;

    public ProduceInfinite(int batchSize) {
        this.batchSize = batchSize;
    }

    @Override
    public int calculateStockDeficit(AggregateItemStack stockChestsStock) {
        return batchSize;
    }

    @Override
    public ProductionStrategyType getType() {
       return ProductionStrategyType.PRODUCE_INFINITE;
    }
}
