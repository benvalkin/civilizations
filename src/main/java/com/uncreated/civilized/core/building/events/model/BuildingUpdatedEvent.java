package com.uncreated.civilized.core.building.events.model;

import com.uncreated.civilized.core.building.Building;

import lombok.Getter;
import net.neoforged.bus.api.Event;

@Getter
public class BuildingUpdatedEvent extends Event {

   public BuildingUpdatedEvent(Building building, boolean isClientside) {
      this.building = building;
      this.isClientside = isClientside;
   }

   private final Building building;
   private final boolean isClientside;
}
