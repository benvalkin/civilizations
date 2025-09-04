package com.uncreated.civilized.core.building.behaviour;

import com.uncreated.civilized.core.building.Building;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;

public class CropFarmBehaviour extends BuildingBehaviour {

   protected CropFarmBehaviour(Building building) {
      super(building);
   }

   public void applyNbt(CompoundTag compoundTag) {

   }
   public CompoundTag toNbt() {
      return new CompoundTag();
   }
}
