package com.uncreated.civilized.core.building.state;

import net.minecraft.core.HolderLookup;
import net.minecraft.server.level.ServerLevel;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;
import com.uncreated.civilized.core.building.Building;

import net.minecraft.nbt.CompoundTag;

public class BuildingState {

   protected final Logger LOGGER = LogUtils.getLogger();
   protected final Building building;

   protected BuildingState(Building building) {
      this.building = building;
   }

   public static BuildingState create(Building building) {
      return switch (building.getBuildingType()) {
      case BAKER_HOUSE -> new BakeryState(building);
      case CROP_FARM -> new CropFarmState(building);
      case GROVE -> new GroveState(building);
      case CATTLE_FARM, SHEEP_FARM, HOG_FARM, CHICKEN_FARM -> new AnimalFarmState(building);
      default -> new BuildingState(building) {
      };
      };
   }

   public void applyNbt(CompoundTag compoundTag, HolderLookup.Provider registryAccess) {

   }

   public CompoundTag toNbt(HolderLookup.Provider registryAccess) {
      return new CompoundTag();
   }

   public void serverAddToBuildingScreenContext(CompoundTag compoundTag, ServerLevel serverLevel) {

   }
}
