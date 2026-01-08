package com.uncreated.civilized.core.building.crafting.orders;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import com.uncreated.civilized.core.building.crafting.PendingProductionOutput;
import com.uncreated.civilized.core.building.crafting.bills.ProductionBill;
import com.uncreated.civilized.core.building.crafting.orders.recipe.AssembledRecipe;
import com.uncreated.civilized.core.building.crafting.orders.recipe.CraftingRecipeAssembler;
import com.uncreated.civilized.core.building.crafting.orders.recipe.RecipeAssembler;
import com.uncreated.civilized.core.building.crafting.orders.recipe.SingleItemRecipeAssembler;
import com.uncreated.civilized.core.building.logistics.AggregateItemStack;
import com.uncreated.civilized.core.building.logistics.orders.LogisticsOrder;
import com.uncreated.civilized.core.building.logistics.orders.imports.ImportExactly;
import com.uncreated.civilized.core.building.logistics.orders.imports.ImportOrder;

import lombok.Getter;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;

@Getter
public class ProductionOrder {

   private final ProductionBill bill;
   private final String key;
   protected final RecipeAssembler<?, ?> recipeAssembler;
   private final Level level;

   public ProductionOrder(String key, ProductionBill bill, ServerLevel level) {
      this.bill = bill;
      this.key = key;

      Optional<RecipeHolder<?>> recipeHolder = bill.resolveRecipe(level.getServer().getRecipeManager());
      if (recipeHolder.isEmpty())
         throw new UnsupportedOperationException("Could not create production order - recipe not found");

      recipeAssembler = switch (bill.getProductionType()) {
      case CRAFTING -> new CraftingRecipeAssembler(recipeHolder.get().value(), level.registryAccess());
      case SMELTING, BLASTING, SMOKING ->
         new SingleItemRecipeAssembler(recipeHolder.get().value(), level.registryAccess());
      };

      this.level = level;
   }

   public List<ImportOrder> createImportOrdersForIngredients(List<Container> stockChests) {

      // compute the current stock deficit so we know how many ingredients to import
      int stockDeficit = 0;

      Optional<ItemStack> defaultResultItem = recipeAssembler.getDefaultResultItem();
      if (defaultResultItem.isPresent()) {
         stockDeficit =
               bill.getProductionStrategy().calculateStockDeficit(calculateStock(stockChests, defaultResultItem.get()));
      }

      List<ImportOrder> importOrders = new LinkedList<>();

      List<Ingredient> ingredients = recipeAssembler.getRecipe().placementInfo().ingredients();

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

   public PendingProductionOutput getNextOutput(List<Container> ingredientsChests, List<Container> stockChests) {

      RecipeAssembler.RecipeSatisfiedResult recipeSatisfied = recipeAssembler.isRecipeSatisfied(ingredientsChests);

      AssembledRecipe<?> assembledRecipe = recipeAssembler.assembleRecipe(recipeSatisfied.availableInput());

      int stockDeficit =
            bill.getProductionStrategy()
                  .calculateStockDeficit(calculateStock(stockChests, assembledRecipe.resultItem()));

      return new PendingProductionOutput(
            recipeAssembler.getRecipe(),
            assembledRecipe,
            recipeSatisfied.satisfied(),
            stockDeficit,
            ingredientsChests);
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
}
