package com.uncreated.civilized.core.building.events.model;

import javax.annotation.Nullable;

import com.uncreated.civilized.core.building.Building;

import lombok.Getter;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.Event;

@Getter
public class BuildingUpdatedEvent extends Event {

   public BuildingUpdatedEvent(Building building, Level level, boolean isClientside) {
      this.building = building;
      this.level = level;
      this.isClientside = isClientside;
   }

   private final Building building;
   private final @Nullable Level level;
   private final boolean isClientside;
}
