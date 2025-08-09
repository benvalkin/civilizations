package com.uncreated.civilized.core.building.behaviour;

import org.slf4j.Logger;

import com.uncreated.civilized.core.building.Building;
import com.mojang.logging.LogUtils;

import net.minecraft.server.level.ServerLevel;

public abstract class BuildingBehaviour {

   protected final Logger LOGGER = LogUtils.getLogger();
   protected final Building building;

   protected BuildingBehaviour(Building building) {
      this.building = building;
   }

   public static BuildingBehaviour create(Building building) {
      return switch (building.getBuildingType()) {
      case INN -> new InnBehaviour(building);
      case INN -> new InnBehaviour(building);
      default -> new InertBuildingBehaviour(building);
      };
   }

   public void start(ServerLevel level, long gameTime) {}
   public void serverTick(ServerLevel level, long gameTime) {}
}
