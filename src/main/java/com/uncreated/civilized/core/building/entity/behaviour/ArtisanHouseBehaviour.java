package com.uncreated.civilized.core.building.entity.behaviour;

import java.util.List;

import com.uncreated.civilized.core.building.entity.LoadedBuilding;
import com.uncreated.civilized.core.building.production.RecipeProductionMachine;
import com.uncreated.civilized.core.building.production.RecipeProductionSystem;
import com.uncreated.civilized.core.building.production.bills.ProductionType;
import com.uncreated.civilized.core.building.state.ArtisanHouseState;

import lombok.Getter;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

public abstract class ArtisanHouseBehaviour extends BuildingBehaviour {

   @Getter
   private RecipeProductionSystem recipeProductionSystem; // null on client

   protected ArtisanHouseBehaviour(LoadedBuilding entity) {
      super(entity);

      if (!entity.getLevel().isClientSide()) {
         recipeProductionSystem = new RecipeProductionSystem();
      }
   }

   protected abstract void registerProductionMachines(RecipeProductionSystem recipeProductionSystem);

   @Override
   public void start() {

      ArtisanHouseState artisanHouseState = (ArtisanHouseState) getBuilding().getState();

      registerProductionMachines(recipeProductionSystem);

      for (RecipeProductionMachine<?> machine : recipeProductionSystem.registeredMachines()) {
         artisanHouseState.createProductionOrders(machine, (ServerLevel) getEntity().getLevel());
      }
   }
}
