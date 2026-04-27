package com.uncreated.civilized.core.building.entity.behaviour;

import com.uncreated.civilized.core.building.entity.LoadedBuilding;
import com.uncreated.civilized.core.building.production.RecipeProductionSystem;
import com.uncreated.civilized.core.building.production.lines.singleitem.cooking.SmokingMachine;

public class ButcheryBehaviour extends CraftsmanHouseBehaviour {
   protected ButcheryBehaviour(LoadedBuilding entity) {
      super(entity);
   }

   @Override
   protected void registerProductionMachines(RecipeProductionSystem recipeProductionSystem) {
      super.registerProductionMachines(recipeProductionSystem);
      recipeProductionSystem.registerMachine(SmokingMachine.class, new SmokingMachine());
   }
}
