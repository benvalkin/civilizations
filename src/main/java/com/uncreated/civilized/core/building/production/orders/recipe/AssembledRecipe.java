package com.uncreated.civilized.core.building.production.orders.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

import java.util.List;

public record AssembledRecipe<TRecipeInput extends RecipeInput>(List<ItemStack> availableIngredients, TRecipeInput assembledInput, ItemStack resultItem) {

}
