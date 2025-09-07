package com.uncreated.civilized.core.building.behaviour;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;
import com.uncreated.civilized.core.building.Building;

import net.minecraft.nbt.CompoundTag;
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
      case CROP_FARM -> new CropFarmBehaviour(building);
      case GROVE -> new GroveBehaviour(building);
      case CATTLE_FARM, SHEEP_FARM, HOG_FARM, CHICKEN_FARM -> new AnimalFarmBehaviour(building);
      default -> new InertBuildingBehaviour(building);
      };
   }

   public void start(ServerLevel level, long gameTime) {
   }

   public void serverTick(ServerLevel level, long gameTime) {
   }

   public void applyNbt(CompoundTag compoundTag) {

   }

   public CompoundTag toNbt() {
      return new CompoundTag();
   }
}
