package com.uncreated.civilized.core.building.entity.behaviour;

import com.uncreated.civilized.core.building.entity.LoadedBuilding;
import com.uncreated.civilized.core.building.production.RecipeProductionSystem;
import com.uncreated.civilized.core.building.production.lines.crafting.CraftingMachine;
import com.uncreated.civilized.core.building.production.lines.singleitem.cooking.SmeltingMachine;

public class BakeryBehaviour extends ArtisanHouseBehaviour {
   protected BakeryBehaviour(LoadedBuilding entity) {
      super(entity);
   }

   @Override
   protected void registerProductionMachines(RecipeProductionSystem recipeProductionSystem) {
      recipeProductionSystem.registerMachine(CraftingMachine.class, new CraftingMachine());
      recipeProductionSystem.registerMachine(SmeltingMachine.class, new SmeltingMachine());
   }
}
