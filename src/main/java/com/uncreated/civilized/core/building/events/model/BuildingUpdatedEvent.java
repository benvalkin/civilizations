package com.uncreated.civilized.core.building.events.model;

import javax.annotation.Nullable;

import com.uncreated.civilized.core.building.Building;

import com.uncreated.civilized.core.building.entity.LoadedBuildings;
import lombok.Getter;
import net.minecraft.world.level.Level;
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
