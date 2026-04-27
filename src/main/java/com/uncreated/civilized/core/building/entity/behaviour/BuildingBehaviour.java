package com.uncreated.civilized.core.building.entity.behaviour;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;
import com.uncreated.civilized.core.building.Building;
import com.uncreated.civilized.core.building.entity.LoadedBuilding;

import lombok.Getter;
import net.minecraft.server.level.ServerLevel;

public class BuildingBehaviour {

   protected final Logger LOGGER = LogUtils.getLogger();

   @Getter
   private final LoadedBuilding entity;

   public Building getBuilding() {
      return entity.getBuilding();
   }

   public BuildingBehaviour(LoadedBuilding entity) {
      this.entity = entity;
   }

   public void start() {

   }

   public void serverTick(ServerLevel level, long gameTime) {
   }

   public static BuildingBehaviour create(LoadedBuilding entity) {
      return switch (entity.getBuilding().getBuildingType()) {
      case INN -> new InnBehaviour(entity);
      case BAKER_HOUSE -> new BakeryBehaviour(entity);
      case BUTCHER_HOUSE -> new ButcheryBehaviour(entity);
      case BLACKSMITH_HOUSE -> new BlacksmithHouseBehaviour(entity);
      case MASON_HOUSE -> new MasonHouseBehaviour(entity);
      case TOOLSMITH_HOUSE, WEAPONSMITH_HOUSE, ARMORER_HOUSE, LEATHERWORKER_HOUSE, WEAVER_HOUSE, CARPENTER_HOUSE, FLETCHER_HOUSE,
           CARTOGRAPHER_HOUSE, ARTIST_HOUSE ->
         new CraftsmanHouseBehaviour(entity);
      default -> new BuildingBehaviour(entity);
      };
   }
}
