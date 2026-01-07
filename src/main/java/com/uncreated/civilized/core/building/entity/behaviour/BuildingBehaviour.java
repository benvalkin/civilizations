package com.uncreated.civilized.core.building.entity.behaviour;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;
import com.uncreated.civilized.core.building.Building;
import com.uncreated.civilized.core.building.crafting.CraftingMachine;
import com.uncreated.civilized.core.building.entity.LoadedBuilding;

import lombok.Getter;
import net.minecraft.server.level.ServerLevel;

public class BuildingBehaviour {

   protected final Logger LOGGER = LogUtils.getLogger();

   @Getter
   private final CraftingMachine craftingMachine;

   @Getter
   private final LoadedBuilding entity;

   public Building getBuilding() {
      return entity.getBuilding();
   }

   public BuildingBehaviour(LoadedBuilding entity) {
      this.entity = entity;
      craftingMachine = new CraftingMachine();
   }

   public void start() {

   }

   public void serverTick(ServerLevel level, long gameTime) {
   }

   public static BuildingBehaviour create(LoadedBuilding entity) {
      return switch (entity.getBuilding().getBuildingType()) {
      case INN -> new InnBehaviour(entity);
      case BAKER_HOUSE -> new ArtisanHouseBehaviour(entity);
      default -> new BuildingBehaviour(entity);
      };
   }
}
