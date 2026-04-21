package com.uncreated.civilized.core.building.production.lines.singleitem.cooking;

import com.uncreated.civilized.core.building.production.bills.ProductionType;

public class SmokingMachine extends CookingMachine {

   @Override
   public ProductionType getProductionType() {
      return ProductionType.SMOKING;
   }
}
