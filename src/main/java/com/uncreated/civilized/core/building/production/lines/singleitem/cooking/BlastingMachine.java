package com.uncreated.civilized.core.building.production.lines.singleitem.cooking;

import com.uncreated.civilized.core.building.production.bills.ProductionType;
import com.uncreated.civilized.core.building.production.bills.ProductionTypes;

public class BlastingMachine extends CookingMachine {

   @Override
   public ProductionType getProductionType() {
      return ProductionTypes.BLASTING;
   }
}
