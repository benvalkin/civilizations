package com.uncreated.civilized.core.building.state;

import java.util.List;
import java.util.Optional;

import com.uncreated.civilized.core.building.Building;

import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;

public class BakerHouseState extends ArtisanHouseState {
   protected BakerHouseState(Building building) {
      super(building);
   }

   @Override
   public List<Optional<RecipeHolder<CraftingRecipe>>> getDefaultCraftingRecipes(RecipeManager recipeManager) {
      return List.of(
            getCraftingRecipeFor(recipeManager, "minecraft:bread"),
            getCraftingRecipeFor(recipeManager, "minecraft:iron_pickaxe"));
   }
}
