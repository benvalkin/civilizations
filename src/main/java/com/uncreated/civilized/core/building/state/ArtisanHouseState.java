package com.uncreated.civilized.core.building.state;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import com.uncreated.civilized.core.building.Building;
import com.uncreated.civilized.core.building.crafting.bills.ProductionBill;
import com.uncreated.civilized.core.building.crafting.bills.ProductionType;
import com.uncreated.civilized.core.building.crafting.orders.ProductionOrder;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;

public class ArtisanHouseState extends BuildingState {
   protected ArtisanHouseState(Building building) {
      super(building);
   }

   public List<ProductionOrder> createProductionOrders(ServerLevel serverLevel) {

      RecipeManager recipeManager = serverLevel.getServer().getRecipeManager();

      List<ProductionBill> bills = getDefaultProductionBills();

      List<ProductionOrder> productionOrders = new LinkedList<>();
      for (int i = 0; i < bills.size(); i++) {

         ProductionBill bill = bills.get(i);

         Optional<RecipeHolder<?>> recipe = bill.resolveRecipe(recipeManager);

         if (recipe.isEmpty())
            continue;

         String key = "order_" + i;
         productionOrders.add(new ProductionOrder(key, bill, serverLevel));
      }

      return productionOrders;
   }

   public List<ProductionBill> getDefaultProductionBills() {
      return List.of();
   }
}
