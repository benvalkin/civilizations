package com.uncreated.civilized.core.building.state;

import java.util.List;

import com.uncreated.civilized.core.building.Building;
import com.uncreated.civilized.core.building.production.bills.ProductionBill;
import com.uncreated.civilized.core.building.production.bills.ProductionType;
import com.uncreated.civilized.core.building.production.bills.strategy.ProductionStrategyType;
import com.uncreated.civilized.tag.CommonTags;

import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeInput;
import net.neoforged.neoforge.common.Tags;

public class BakeryState extends ArtisanHouseState {
   protected BakeryState(Building building) {
      super(building);
   }

   @Override
   protected List<ProductionType> getSupportedProductionTypes() {
      return List.of(ProductionType.CRAFTING, ProductionType.SMELTING);
   }

   @Override
   protected List<ProductionBill> getDefaultProductionBills(ProductionType productionType) {

      return switch (productionType) {
      case CRAFTING -> List.of(
            new ProductionBill(
                  "minecraft:bread",
                  ProductionType.CRAFTING,
                  ProductionStrategyType.PRODUCE_UP_TO,
                  64,
                  true,
                  List.of(new ItemStack(Items.WHEAT), new ItemStack(Items.WHEAT), new ItemStack(Items.WHEAT)),
                  new ItemStack(Items.BREAD)));
      default -> List.of();
      };
   }

   private static final List<TagKey<Item>> ALLOWED_RECIPE_TAGS =
         List.of(
               Tags.Items.CROPS_WHEAT,
               Tags.Items.FOODS_BREAD,
               CommonTags.CROPS_GRAIN,
               CommonTags.FOODS_DOUGH,
               CommonTags.FOODS_DOUGH_WHEAT,
               CommonTags.FOODS_PASTA,
               CommonTags.FLOURS,
               CommonTags.FLOURS_WHEAT);

   @Override
   public boolean recipeAllowed(ProductionType productionType, RecipeInput recipeInput, ServerLevel level) {
      return recipeHasAtLeastOneIngredientWithTag(recipeInput, ALLOWED_RECIPE_TAGS);
   }

   @Override
   public Tooltip getAllowedRecipeHelpTooltip() {
      return Tooltip.create(
            Component.translatable(
                    "menu.building.residence.production_bills.edit_recipe.tooltip.allowed_recipe_help.baker"));
   }
}
