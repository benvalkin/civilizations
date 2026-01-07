package com.uncreated.civilized.core.building.events.model;

import com.uncreated.civilized.core.building.Building;

import net.minecraft.world.level.Level;

public class BuildingDeletedEvent extends BuildingUpdatedEvent {

   public BuildingDeletedEvent(Building building, boolean isClientside) {
      super(building, isClientside);
   }
}
