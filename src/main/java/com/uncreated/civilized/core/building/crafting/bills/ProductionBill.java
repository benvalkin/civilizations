package com.uncreated.civilized.core.building.crafting.bills;

import java.util.Optional;

import com.uncreated.civilized.core.building.crafting.bills.strategy.IProductionStrategy;
import com.uncreated.civilized.core.building.crafting.bills.strategy.ProduceInfinite;
import com.uncreated.civilized.core.building.crafting.bills.strategy.ProduceUpTo;

import com.uncreated.civilized.core.building.crafting.bills.strategy.ProductionStrategyType;
import lombok.Getter;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;

@Getter
public class ProductionBill {

   private final String minecraftRecipeName;
   private final ProductionType productionType;
   private final IProductionStrategy productionStrategy;
   private final int billAmount;
   private final boolean checkStorehouse;

   public ProductionBill(
         String minecraftRecipeName,
         ProductionType productionType,
         ProductionStrategyType productionStrategyType,
         int billAmount,
         boolean checkStorehouse) {
      this.minecraftRecipeName = minecraftRecipeName;
      this.productionType = productionType;
      this.billAmount = billAmount;
      this.checkStorehouse = checkStorehouse;
      this.productionStrategy = switch (productionStrategyType) {
      case ProductionStrategyType.PRODUCE_INFINITE -> new ProduceInfinite(billAmount);
      case ProductionStrategyType.PRODUCE_UP_TO -> new ProduceUpTo(billAmount);
      };
   }

   public Optional<RecipeHolder<?>> resolveRecipe(RecipeManager recipeManager) {

      ResourceKey<Recipe<?>> recipeKey =
            ResourceKey.create(Registries.RECIPE, ResourceLocation.parse(minecraftRecipeName));

      return recipeManager.byKey(recipeKey);
   }
}
