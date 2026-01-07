package com.uncreated.civilized.core.building.crafting.orders;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import com.uncreated.civilized.core.building.crafting.PendingProductionOutput;
import com.uncreated.civilized.core.building.logistics.AggregateItemStack;
import com.uncreated.civilized.core.building.logistics.orders.LogisticsOrder;
import com.uncreated.civilized.core.building.logistics.orders.imports.ImportExactly;
import com.uncreated.civilized.core.building.logistics.orders.imports.ImportOrder;

import it.unimi.dsi.fastutil.ints.IntList;
import lombok.Getter;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;

@Getter
public abstract class ProductionOrder {

   private final Level level;
   private final String key;

   protected final CraftingRecipe recipe;

   public ProductionOrder(Level level, String key, CraftingRecipe recipe) {
      this.level = level;
      this.key = key;
      this.recipe = recipe;
   }

   public List<ImportOrder> createImportOrdersForIngredients(List<Container> stockChests) {

      // compute the current stock deficit so we know how many ingredients to import
      int stockDeficit = 0;

      Optional<ItemStack> defaultResultItem = getDefaultResultItem();
      if (defaultResultItem.isPresent()) {
         stockDeficit = calculateStockDeficit(calculateStock(stockChests, defaultResultItem.get()));
      }

      List<ImportOrder> importOrders = new LinkedList<>();

      List<Ingredient> ingredients = recipe.placementInfo().ingredients();

      for (int i = 0; i < ingredients.size(); i++) {
         Ingredient ingredient = ingredients.get(i);
         ImportOrder importOrder =
               new ImportExactly(
                     level,
                     getKey() + "_ingredient_" + i,
                     in -> ingredient.acceptsItem(in.getItemHolder()),
                     LogisticsOrder.Origin.AUTOMATIC,
                     stockDeficit);
         importOrders.add(importOrder);
      }

      return importOrders;
   }

   protected abstract int calculateStockDeficit(AggregateItemStack stockChestsStock);

   public PendingProductionOutput getNextOutput(List<Container> ingredientsChests, List<Container> stockChests) {

      RecipeSatisfiedResult recipeSatisfied = recipeSatisfied(ingredientsChests);

      CraftingInput craftingInput = CraftingInput.of(3, 3, recipeSatisfied.availableInput);

      ItemStack resultItem = recipe.assemble(craftingInput, level.registryAccess());

      int stockDeficit = calculateStockDeficit(calculateStock(stockChests, resultItem));

      return new PendingProductionOutput(
            recipe,
            craftingInput,
            resultItem,
            recipeSatisfied.satisfied,
            stockDeficit,
            ingredientsChests);
   }

   protected Optional<ItemStack> getDefaultResultItem() {
      // the only way to get the output itemStack of a recipe is by passing in a dummy crafting input.
      // for crafting recipes, this input list can be empty - however, this may not work for other recipes
      CraftingInput dummyCraftingInput = CraftingInput.Positioned.EMPTY.input();
      ItemStack resultItem = recipe.assemble(dummyCraftingInput, level.registryAccess());
      if (resultItem.isEmpty())
         return Optional.empty();

      return Optional.of(resultItem);
   }

   protected RecipeSatisfiedResult recipeSatisfied(List<Container> containers) {

      // 3x3 "crafting window" in array form
      ItemStack[] craftingWindow = new ItemStack[9];
      Arrays.fill(craftingWindow, ItemStack.EMPTY);

      IntList slotsToIngredientIndex = recipe.placementInfo().slotsToIngredientIndex();
      List<Ingredient> ingredients = recipe.placementInfo().ingredients();

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

   protected AggregateItemStack calculateStock(Collection<Container> containers, ItemStack resultItem) {
      AggregateItemStack stock = new AggregateItemStack();
      containers.forEach(c -> {
         for (int i = 0; i < c.getContainerSize(); i++) {
            ItemStack itemStack = c.getItem(i);
            if (itemStack.isEmpty())
               continue;

            if (!itemStack.is(resultItem.getItem()))
               continue;

            stock.add(itemStack);
         }
      });

      return stock;
   }

   public record RecipeSatisfiedResult(boolean satisfied, int ingredientsSatisfied, int totalIngredients,
         List<ItemStack> availableInput) {

   }
}
