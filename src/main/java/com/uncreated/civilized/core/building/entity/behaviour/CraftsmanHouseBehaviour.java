package com.uncreated.civilized.core.building.entity.behaviour;

import com.uncreated.civilized.core.building.entity.LoadedBuilding;
import com.uncreated.civilized.core.building.production.RecipeProductionSystem;
import com.uncreated.civilized.core.building.production.lines.crafting.CraftingMachine;

/**
 * A type of Artisan house that only supports crafting production and no other production types.
 */
public class CraftsmanHouseBehaviour extends ArtisanHouseBehaviour {
   protected CraftsmanHouseBehaviour(LoadedBuilding entity) {
      super(entity);
   }

   @Override
   protected void registerProductionMachines(RecipeProductionSystem recipeProductionSystem) {
      recipeProductionSystem.registerMachine(CraftingMachine.class, new CraftingMachine());
   }
}
