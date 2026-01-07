package com.uncreated.civilized.core.building.entity;

import com.uncreated.civilized.core.building.Building;
import com.uncreated.civilized.core.building.entity.behaviour.BuildingBehaviour;

import lombok.Getter;
import net.minecraft.world.level.Level;

@Getter
public class LoadedBuilding {
   private final Building building;
   private final Level level;
   private final BuildingBehaviour behaviour;

   public LoadedBuilding(Building building, Level level) {
      this.building = building;
      this.level = level;
      behaviour = BuildingBehaviour.create(this);
      behaviour.start();
   }
}
