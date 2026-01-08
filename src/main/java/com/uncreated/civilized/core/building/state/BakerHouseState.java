package com.uncreated.civilized.core.building.state;

import java.util.List;

import com.uncreated.civilized.core.building.Building;
import com.uncreated.civilized.core.building.crafting.bills.ProductionBill;
import com.uncreated.civilized.core.building.crafting.bills.ProductionType;
import com.uncreated.civilized.core.building.crafting.bills.strategy.ProductionStrategyType;

public class BakerHouseState extends ArtisanHouseState {
   protected BakerHouseState(Building building) {
      super(building);
   }

   @Override
   public List<ProductionBill> getDefaultProductionBills() {
      return List.of(
            new ProductionBill(
                  "minecraft:bread",
                  ProductionType.CRAFTING,
                  ProductionStrategyType.PRODUCE_UP_TO,
                  32,
                  true));
   }
}
