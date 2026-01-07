package com.uncreated.civilized.core.building.crafting;

import java.util.Collection;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;

public record PendingProductionOutput(CraftingRecipe recipe, CraftingInput craftingInput, ItemStack resultItem, boolean canProduce, int stockDeficit,
      Collection<Container> sourceContainers) {

   public void consumeIngredients(ServerLevel level) {

      for (ItemStack ingredient : craftingInput.items()) {

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
