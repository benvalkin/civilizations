package com.uncreated.civilized.core.building.entity.behaviour;

import java.util.List;

import com.uncreated.civilized.core.building.entity.LoadedBuilding;
import com.uncreated.civilized.core.building.production.lines.crafting.CraftingMachine;
import com.uncreated.civilized.core.building.production.lines.crafting.CraftingOrder;
import com.uncreated.civilized.core.building.state.ArtisanHouseState;

import net.minecraft.server.level.ServerLevel;

public class ArtisanHouseBehaviour extends BuildingBehaviour {

   protected ArtisanHouseBehaviour(LoadedBuilding entity) {
      super(entity);
   }

   @Override
   public void start() {

      ArtisanHouseState artisanHouseState = (ArtisanHouseState) getBuilding().getState();

      // CRAFTING

      CraftingMachine machine =
            getEntity().getBehaviour().getRecipeProductionSystem().getMachine(CraftingMachine.class);

      List<CraftingOrder> productionOrders =
            artisanHouseState.createProductionOrders(
                  getEntity().getBehaviour().getRecipeProductionSystem().getMachine(CraftingMachine.class),
                  (ServerLevel) getEntity().getLevel());

      for (CraftingOrder productionOrder : productionOrders) {
         machine.registerOrder(productionOrder);
      }
   }
}
