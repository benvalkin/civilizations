package com.uncreated.civilized.core.building.production.lines.singleitem;

import java.util.List;
import java.util.Optional;

import com.uncreated.civilized.core.building.production.orders.recipe.AssembledRecipe;
import com.uncreated.civilized.core.building.production.orders.recipe.RecipeAssembler;
import lombok.Getter;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.SingleItemRecipe;
import net.minecraft.world.item.crafting.SingleRecipeInput;

public class SingleItemRecipeAssembler extends RecipeAssembler<SingleItemRecipe, SingleRecipeInput> {

   @Getter
   private final SingleItemRecipe recipe;
   private final HolderLookup.Provider registryAccess;

   public SingleItemRecipeAssembler(Recipe<?> recipe, HolderLookup.Provider registryAccess) {
      super(registryAccess);
      if (!(recipe instanceof SingleItemRecipe singleItemRecipe)) {
         throw new UnsupportedOperationException("Unsupported recipe type: " + recipe);
      }

      this.recipe = singleItemRecipe;
      this.registryAccess = registryAccess;
   }

   public AssembledRecipe<SingleRecipeInput> assembleRecipe(List<ItemStack> availableIngredients) {
      SingleRecipeInput input = new SingleRecipeInput(availableIngredients.getFirst());
      ItemStack result = recipe.assemble(input, registryAccess);
      return new AssembledRecipe<>(availableIngredients, input, result);
   }

   public Optional<ItemStack> getDefaultResultItem() {
      // the only way to get the output itemStack of a recipe is by passing in a dummy recipe input.
      // for crafting and single item recipes, this input list can be empty - however, this may not work for other
      // recipes
      SingleRecipeInput dummyCraftingInput = new SingleRecipeInput(ItemStack.EMPTY);
      ItemStack resultItem = recipe.assemble(dummyCraftingInput, registryAccess);
      if (resultItem.isEmpty())
         return Optional.empty();

      return Optional.of(resultItem);
   }
}
