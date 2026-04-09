package com.uncreated.civilized.core.building.entity.behaviour;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;
import com.uncreated.civilized.core.building.Building;
import com.uncreated.civilized.core.building.entity.LoadedBuilding;
import com.uncreated.civilized.core.building.production.RecipeProductionMachineRegisteredEvent;
import com.uncreated.civilized.core.building.production.RecipeProductionSystem;
import com.uncreated.civilized.core.building.production.lines.crafting.CraftingMachine;
import com.uncreated.civilized.core.building.production.lines.singleitem.smelting.SmeltingMachine;

import lombok.Getter;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.common.NeoForge;

public class BuildingBehaviour {

   protected final Logger LOGGER = LogUtils.getLogger();

   @Getter
   private RecipeProductionSystem recipeProductionSystem; // null on client

   @Getter
   private final LoadedBuilding entity;

   public Building getBuilding() {
      return entity.getBuilding();
   }

   public BuildingBehaviour(LoadedBuilding entity) {
      this.entity = entity;

      if (!entity.getLevel().isClientSide()) {
         recipeProductionSystem = new RecipeProductionSystem();
         registerBuiltInProductionMachines(this, recipeProductionSystem);
      }
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

   private void registerBuiltInProductionMachines(
         BuildingBehaviour behaviour,
         RecipeProductionSystem recipeProductionSystem) {

      if (behaviour instanceof ArtisanHouseBehaviour) {
         recipeProductionSystem.registerMachine(CraftingMachine.class, new CraftingMachine());
         recipeProductionSystem.registerMachine(SmeltingMachine.class, new SmeltingMachine());
      }

      NeoForge.EVENT_BUS.post(new RecipeProductionMachineRegisteredEvent(behaviour, recipeProductionSystem));
   }
}
