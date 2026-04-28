package com.uncreated.civilized.core.building.state;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;
import com.uncreated.civilized.core.building.Building;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;

public class BuildingState {

   protected final Logger LOGGER = LogUtils.getLogger();
   protected final Building building;

   public BuildingState(Building building) {
      this.building = building;
   }

   public void applyNbt(CompoundTag compoundTag, HolderLookup.Provider registryAccess) {

   }

   public CompoundTag toNbt(HolderLookup.Provider registryAccess) {
      return new CompoundTag();
   }

   public void serverAddToBuildingScreenContext(CompoundTag compoundTag, ServerLevel serverLevel) {

   }
}
