package com.uncreated.civilized.core.building.crafting;

import java.util.Collection;
import java.util.List;

import com.uncreated.civilized.core.building.crafting.orders.recipe.AssembledRecipe;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.SingleRecipeInput;

public record PendingProductionOutput(Recipe<?> recipe, AssembledRecipe<?> assembledRecipe,
      boolean canProduce, int stockDeficit, Collection<Container> sourceContainers) {

   public void consumeIngredients() {

      for (ItemStack ingredient : assembledRecipe.availableIngredients()) {

         final int quota = ingredient.getCount();
         int successfullyRemoved = 0;
         for (Container sourceContainer : sourceContainers) {

            for (int i = 0; i < sourceContainer.getContainerSize(); i++) {
               ItemStack item = sourceContainer.getItem(i);

               if (!item.is(ingredient.getItem()))
                  continue;

               successfullyRemoved += item.getCount();
               item.shrink(quota);
               sourceContainer.setItem(i, item);

               if (successfullyRemoved == quota)
                  break;
            }

            if (successfullyRemoved == quota)
               break;
         }
      }
   }
}
