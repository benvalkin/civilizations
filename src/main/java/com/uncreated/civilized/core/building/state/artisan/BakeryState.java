package com.uncreated.civilized.core.building.state.artisan;

import java.util.Collections;
import java.util.List;

import com.uncreated.civilized.core.building.Building;
import com.uncreated.civilized.core.building.production.bills.ProductionBill;
import com.uncreated.civilized.core.building.production.bills.ProductionType;
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
   public BakeryState(Building building) {
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
            createDefaultBill(
                  "minecraft:bread",
                  ProductionType.CRAFTING,
                  Collections.nCopies(3, new ItemStack(Items.WHEAT)),
                  new ItemStack(Items.BREAD)),
            createDefaultBill(
                  "minecraft:sugar",
                  ProductionType.CRAFTING,
                  List.of(new ItemStack(Items.SUGAR_CANE)),
                  new ItemStack(Items.SUGAR)));
      case SMELTING -> List.of(
            createDefaultBill(
                  "minecraft:baked_potato",
                  ProductionType.SMELTING,
                  List.of(new ItemStack(Items.POTATO)),
                  new ItemStack(Items.BAKED_POTATO)));
      default -> List.of();
      };
   }

   private static final List<TagKey<Item>> ALLOWED_INGREDIENT_TAGS =
         List.of(
               Tags.Items.CROPS_WHEAT,
               Tags.Items.FOODS_BREAD,
               CommonTags.CROPS_GRAIN,
               CommonTags.FOODS_DOUGH,
               CommonTags.FOODS_DOUGH_WHEAT,
               CommonTags.FOODS_PASTA,
               CommonTags.FLOURS,
               CommonTags.FLOURS_WHEAT);

   private static final List<Item> ALLOWED_OUTPUT_ITEMS = List.of(Items.SUGAR);

   @Override
   public boolean recipeAllowed(
         ProductionType productionType,
         RecipeInput recipeInput,
         ItemStack resultItem,
         ServerLevel level) {
      return recipeHasAtLeastOneIngredientWithMatchingTag(recipeInput, ALLOWED_INGREDIENT_TAGS)
            || itemIsOneOf(resultItem, ALLOWED_OUTPUT_ITEMS);
   }

   @Override
   public Tooltip getAllowedRecipeHelpTooltip() {
      return Tooltip.create(
            Component.translatable(
                  "menu.building.residence.production_bills.edit_recipe.tooltip.allowed_recipe_help.baker"));
   }
}
