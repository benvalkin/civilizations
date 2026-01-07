package com.uncreated.civilized.core.building.state;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import com.uncreated.civilized.core.building.Building;
import com.uncreated.civilized.core.building.crafting.orders.ProduceUpTo;
import com.uncreated.civilized.core.building.crafting.orders.ProductionOrder;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;

public class ArtisanHouseState extends BuildingState {
   protected ArtisanHouseState(Building building) {
      super(building);
   }

   public List<ProductionOrder> createProductionOrders(ServerLevel serverLevel) {

      RecipeManager recipeManager = serverLevel.getServer().getRecipeManager();

      List<Optional<RecipeHolder<CraftingRecipe>>> craftingRecipes = getDefaultCraftingRecipes(recipeManager);

      List<ProductionOrder> productionOrders = new LinkedList<>();
      for (int i = 0; i < craftingRecipes.size(); i++) {
         Optional<RecipeHolder<CraftingRecipe>> recipe = craftingRecipes.get(i);
         if (recipe.isEmpty())
            continue;

         String key = "order_" + i;
         productionOrders.add(new ProduceUpTo(serverLevel, key, recipe.get().value(), 32));
      }

      return productionOrders;
   }

   public List<Optional<RecipeHolder<CraftingRecipe>>> getDefaultCraftingRecipes(RecipeManager recipeManager) {
      return List.of();
   }

   protected Optional<RecipeHolder<CraftingRecipe>> getCraftingRecipeFor(
         RecipeManager recipeManager,
         String recipeName) {

      ResourceKey<Recipe<?>> recipeKey = ResourceKey.create(Registries.RECIPE, ResourceLocation.parse(recipeName));

      Optional<RecipeHolder<?>> recipeHolder = recipeManager.byKey(recipeKey);

      if (recipeHolder.isPresent() && recipeHolder.get().value() instanceof CraftingRecipe craftingRecipe)
         return Optional.of(new RecipeHolder<>(recipeKey, craftingRecipe));

      return Optional.empty();
   }
}
