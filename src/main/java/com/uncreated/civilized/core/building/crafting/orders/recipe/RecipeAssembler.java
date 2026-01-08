package com.uncreated.civilized.core.building.crafting.orders.recipe;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;

public abstract class RecipeAssembler<TRecipe extends Recipe<?>, TRecipeInput extends RecipeInput> {

   private final HolderLookup.Provider registryAccess;

   public RecipeAssembler(HolderLookup.Provider registryAccess) {
      this.registryAccess = registryAccess;
   }

   public abstract TRecipe getRecipe();

   public abstract AssembledRecipe<TRecipeInput> assembleRecipe(List<ItemStack> availableInput);

   public abstract Optional<ItemStack> getDefaultResultItem();

   public RecipeSatisfiedResult isRecipeSatisfied(List<Container> containers) {

      // 3x3 "crafting window" in array form
      ItemStack[] craftingWindow = new ItemStack[9];
      Arrays.fill(craftingWindow, ItemStack.EMPTY);

      IntList slotsToIngredientIndex = getRecipe().placementInfo().slotsToIngredientIndex();
      List<Ingredient> ingredients = getRecipe().placementInfo().ingredients();

      int ingredientsSatisfied = 0;

      for (int c = 0; c < containers.size(); c++) {
         Container container = containers.get(c);
         for (int s = 0; s < container.getContainerSize(); s++) {

            ItemStack itemStack = container.getItem(s);
            if (itemStack.isEmpty())
               continue;

            // copy the candidate ingredient's item stack because we will decrement it later as we "add it" to the
            // "crafting window"
            ItemStack candidateIngredient = container.getItem(s).copy();

            // check each ingredient to see if the candidate matches, at the same time building the final crafting
            // window

            for (int i = 0; i < slotsToIngredientIndex.size(); i++) {

               int ingredientIndex = slotsToIngredientIndex.getInt(i);
               if (ingredientIndex == -1) // crafting window slots remain empty have a -1 ingredient index
                  continue;

               if (!craftingWindow[i].isEmpty())
                  continue; // ignore this ingredient as we've already using it

               Ingredient ingredient = ingredients.get(ingredientIndex);

               if (ingredient.acceptsItem(candidateIngredient.getItemHolder())) {
                  // if this item is a valid ingredient for this slot, move it to the "crafting window"
                  craftingWindow[i] = candidateIngredient.copyWithCount(1);
                  ingredientsSatisfied++;
                  // mark that we have successfully moved 1 of this stack's item into the "crafting window"
                  candidateIngredient.shrink(1);

                  // if we have satisfied all the required ingredients, we have successfully satisfied this recipe :)
                  if (ingredientsSatisfied == ingredients.size())
                     return new RecipeSatisfiedResult(
                           true,
                           ingredientsSatisfied,
                           ingredients.size(),
                           Arrays.stream(craftingWindow).toList());

                  if (candidateIngredient.isEmpty())
                     // stop if we've used up this candidate ingredient, and move on to the next one
                     break;
               }
            }

         }
      }

      // otherwise, we don't have the correct ingredients to satisfy the recipe
      return new RecipeSatisfiedResult(
            false,
            ingredientsSatisfied,
            ingredients.size(),
            Arrays.stream(craftingWindow).toList());
   }

   public record RecipeSatisfiedResult(boolean satisfied, int ingredientsSatisfied, int totalIngredients,
         List<ItemStack> availableInput) {

   }
}
