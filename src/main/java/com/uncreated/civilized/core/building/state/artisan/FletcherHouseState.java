package com.uncreated.civilized.core.building.state.artisan;

import java.util.List;

import com.uncreated.civilized.core.building.Building;
import com.uncreated.civilized.core.building.production.bills.ProductionBill;
import com.uncreated.civilized.core.building.production.bills.ProductionType;

import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeInput;
import net.neoforged.neoforge.common.Tags;

public class FletcherHouseState extends ArtisanHouseState {
   public FletcherHouseState(Building building) {
      super(building);
   }

   @Override
   protected List<ProductionType> getSupportedProductionTypes() {
      return List.of(ProductionType.CRAFTING);
   }

   @Override
   protected List<ProductionBill> getDefaultProductionBills(ProductionType productionType) {
      return switch (productionType) {
      case CRAFTING -> List.of(
            createDefaultBill(
                  "minecraft:bow",
                  ProductionType.CRAFTING,
                  List.of(
                        ItemStack.EMPTY,
                        new ItemStack(Items.STICK),
                        new ItemStack(Items.STRING),
                        new ItemStack(Items.STICK),
                        ItemStack.EMPTY,
                        new ItemStack(Items.STRING),
                        ItemStack.EMPTY,
                        new ItemStack(Items.STICK),
                        new ItemStack(Items.STRING)),
                  new ItemStack(Items.BOW)),
            createDefaultBill(
                  "minecraft:arrow",
                  ProductionType.CRAFTING,
                  List.of(
                        ItemStack.EMPTY,
                        new ItemStack(Items.FLINT),
                        ItemStack.EMPTY,
                        ItemStack.EMPTY,
                        new ItemStack(Items.STICK),
                        ItemStack.EMPTY,
                        ItemStack.EMPTY,
                        new ItemStack(Items.FEATHER),
                        ItemStack.EMPTY),
                  new ItemStack(Items.ARROW)),
            createDefaultBill(
                  "minecraft:fishing_rod",
                  ProductionType.CRAFTING,
                  List.of(
                        ItemStack.EMPTY,
                        ItemStack.EMPTY,
                        new ItemStack(Items.STICK),
                        ItemStack.EMPTY,
                        new ItemStack(Items.STICK),
                        new ItemStack(Items.STRING),
                        new ItemStack(Items.STICK),
                        ItemStack.EMPTY,
                        new ItemStack(Items.STRING)),
                  new ItemStack(Items.FISHING_ROD)));
      default -> List.of();
      };
   }

   private static final List<TagKey<Item>> ALLOWED_INGREDIENT_TAGS = List.of(Tags.Items.FEATHERS, Tags.Items.STRINGS);
   private static final List<TagKey<Item>> ALLOWED_OUTPUT_TAGS =
         List.of(Tags.Items.TOOLS_BOW, Tags.Items.TOOLS_FISHING_ROD);

   @Override
   public boolean recipeAllowed(
         ProductionType productionType,
         RecipeInput recipeInput,
         ItemStack resultItem,
         ServerLevel level) {
      return recipeHasAtLeastOneIngredientWithMatchingTag(recipeInput, ALLOWED_INGREDIENT_TAGS)
            || itemHasMatchingTag(resultItem, ALLOWED_OUTPUT_TAGS);
   }

   @Override
   public Tooltip getAllowedRecipeHelpTooltip() {
      return Tooltip.create(
            Component.translatable(
                  "menu.building.residence.production_bills.edit_recipe.tooltip.allowed_recipe_help.fletcher"));
   }
}
