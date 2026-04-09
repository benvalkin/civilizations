package com.uncreated.civilized.core.building.production;

import com.uncreated.civilized.core.building.entity.behaviour.BuildingBehaviour;

import lombok.Getter;
import net.neoforged.bus.api.Event;

@Getter
public final class RecipeProductionMachineRegisteredEvent extends Event {
   private final BuildingBehaviour buildingBehaviour;
   private final RecipeProductionSystem recipeProductionSystem;

   public RecipeProductionMachineRegisteredEvent(
         BuildingBehaviour buildingBehaviour,
         RecipeProductionSystem recipeProductionSystem) {
      this.buildingBehaviour = buildingBehaviour;
      this.recipeProductionSystem = recipeProductionSystem;
   }
}
