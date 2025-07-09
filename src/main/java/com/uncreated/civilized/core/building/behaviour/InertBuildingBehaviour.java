package com.uncreated.civilized.core.building.behaviour;

import com.uncreated.civilized.core.building.Building;

import net.minecraft.server.level.ServerLevel;

public class InertBuildingBehaviour extends BuildingBehaviour {

   protected InertBuildingBehaviour(Building building) {
      super(building);
   }

   @Override
   public void serverTick(ServerLevel level, long gameTime) {

   }
}
