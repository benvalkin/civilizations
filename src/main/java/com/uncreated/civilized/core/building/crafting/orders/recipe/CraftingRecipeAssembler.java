package com.uncreated.civilized.core.building.crafting.orders.recipe;

import java.util.List;
import java.util.Optional;

import lombok.Getter;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Recipe;

public class CraftingRecipeAssembler extends RecipeAssembler<CraftingRecipe, CraftingInput> {

    @Getter
    private final CraftingRecipe recipe;
    private final HolderLookup.Provider registryAccess;

    public CraftingRecipeAssembler(Recipe<?> recipe, HolderLookup.Provider registryAccess) {
        super(registryAccess);
        if (!(recipe instanceof CraftingRecipe craftingRecipe)) {
            throw new UnsupportedOperationException("Unsupported recipe type: " + recipe);
        }

        this.recipe = craftingRecipe;
        this.registryAccess = registryAccess;
    }

    public AssembledRecipe<CraftingInput> assembleRecipe(List<ItemStack> availableIngredients) {
        CraftingInput input = CraftingInput.of(3, 3, availableIngredients);
        ItemStack result = recipe.assemble(input, registryAccess);
        return new AssembledRecipe<>(availableIngredients, input, result);
    }

    public Optional<ItemStack> getDefaultResultItem() {
       // the only way to get the output itemStack of a recipe is by passing in a dummy recipe input.
       // for crafting and single item recipes, this input list can be empty - however, this may not work for other
       // recipes
       CraftingInput dummyCraftingInput = CraftingInput.Positioned.EMPTY.input();
       ItemStack resultItem = recipe.assemble(dummyCraftingInput, registryAccess);
       if (resultItem.isEmpty())
          return Optional.empty();

       return Optional.of(resultItem);
    }
}
