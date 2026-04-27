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

public class ArmorerHouseState extends ArtisanHouseState {
   public ArmorerHouseState(Building building) {
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
                  "minecraft:iron_chestplate",
                  ProductionType.CRAFTING,
                  List.of(
                        new ItemStack(Items.IRON_INGOT),
                        ItemStack.EMPTY,
                        new ItemStack(Items.IRON_INGOT),
                        new ItemStack(Items.IRON_INGOT),
                        new ItemStack(Items.IRON_INGOT),
                        new ItemStack(Items.IRON_INGOT),
                        new ItemStack(Items.IRON_INGOT),
                        new ItemStack(Items.IRON_INGOT),
                        new ItemStack(Items.IRON_INGOT)),
                  new ItemStack(Items.IRON_CHESTPLATE)),
            createDefaultBill(
                  "minecraft:iron_leggings",
                  ProductionType.CRAFTING,
                  List.of(
                        new ItemStack(Items.IRON_INGOT),
                        new ItemStack(Items.IRON_INGOT),
                        new ItemStack(Items.IRON_INGOT),
                        new ItemStack(Items.IRON_INGOT),
                        ItemStack.EMPTY,
                        new ItemStack(Items.IRON_INGOT),
                        new ItemStack(Items.IRON_INGOT),
                        ItemStack.EMPTY,
                        new ItemStack(Items.IRON_INGOT)),
                  new ItemStack(Items.IRON_LEGGINGS)));
      default -> List.of();
      };
   }

   private static final List<TagKey<Item>> ALLOWED_OUTPUT_TAGS = List.of(Tags.Items.ARMORS);

   @Override
   public boolean recipeAllowed(
         ProductionType productionType,
         RecipeInput recipeInput,
         ItemStack resultItem,
         ServerLevel level) {
      return itemHasMatchingTag(resultItem, ALLOWED_OUTPUT_TAGS);
   }

   @Override
   public Tooltip getAllowedRecipeHelpTooltip() {
      return Tooltip.create(
            Component.translatable(
                  "menu.building.residence.production_bills.edit_recipe.tooltip.allowed_recipe_help.armorer"));
   }
}
