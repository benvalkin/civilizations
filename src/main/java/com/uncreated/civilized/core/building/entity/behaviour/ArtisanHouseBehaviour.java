package com.uncreated.civilized.core.building.entity.behaviour;

import java.util.List;

import com.uncreated.civilized.core.building.crafting.orders.ProductionOrder;
import com.uncreated.civilized.core.building.entity.LoadedBuilding;
import com.uncreated.civilized.core.building.state.ArtisanHouseState;

import net.minecraft.server.level.ServerLevel;

public class ArtisanHouseBehaviour extends BuildingBehaviour {

   protected ArtisanHouseBehaviour(LoadedBuilding entity) {
      super(entity);
   }

   @Override
   public void start() {

      ArtisanHouseState artisanHouseState = (ArtisanHouseState) getBuilding().getState();

      List<ProductionOrder> productionOrders =
            artisanHouseState.createProductionOrders((ServerLevel) getEntity().getLevel());

      for (ProductionOrder productionOrder : productionOrders) {
         getCraftingMachine().registerOrder(productionOrder);
      }
   }
}
