package com.uncreated.civilized.core.building.state.artisan;

import java.util.List;

import com.uncreated.civilized.core.building.Building;
import com.uncreated.civilized.core.building.production.bills.ProductionBill;
import com.uncreated.civilized.core.building.production.bills.ProductionType;

import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeInput;
import net.neoforged.neoforge.common.Tags;

public class ToolsmithHouseState extends ArtisanHouseState {
   public ToolsmithHouseState(Building building) {
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
                  "minecraft:stone_axe",
                  ProductionType.CRAFTING,
                  List.of(
                        new ItemStack(Items.COBBLESTONE),
                        new ItemStack(Items.COBBLESTONE),
                        ItemStack.EMPTY,
                        new ItemStack(Items.COBBLESTONE),
                        new ItemStack(Items.STICK),
                        ItemStack.EMPTY,
                        ItemStack.EMPTY,
                        new ItemStack(Items.STICK),
                        ItemStack.EMPTY),
                  new ItemStack(Items.STONE_AXE)),
            createDefaultBill(
                  "minecraft:stone_pickaxe",
                  ProductionType.CRAFTING,
                  List.of(
                        new ItemStack(Items.COBBLESTONE),
                        new ItemStack(Items.COBBLESTONE),
                        new ItemStack(Items.COBBLESTONE),
                        ItemStack.EMPTY,
                        new ItemStack(Items.STICK),
                        ItemStack.EMPTY,
                        ItemStack.EMPTY,
                        new ItemStack(Items.STICK),
                        ItemStack.EMPTY),
                  new ItemStack(Items.STONE_PICKAXE)));
      default -> List.of();
      };
   }

   private static final List<TagKey<Item>> ALLOWED_OUTPUT_TAGS = List.of(Tags.Items.TOOLS);
   private static final List<TagKey<Item>> FORBIDDEN_OUTPUT_TAGS =
         List.of(
               ItemTags.SWORDS,
               Tags.Items.TOOLS_BOW,
               Tags.Items.TOOLS_CROSSBOW,
               Tags.Items.TOOLS_SHIELD,
               Tags.Items.TOOLS_MACE);

   @Override
   public boolean recipeAllowed(
         ProductionType productionType,
         RecipeInput recipeInput,
         ItemStack resultItem,
         ServerLevel level) {
      return itemHasMatchingTag(resultItem, ALLOWED_OUTPUT_TAGS)
            && !itemHasMatchingTag(resultItem, FORBIDDEN_OUTPUT_TAGS);
   }

   @Override
   public Tooltip getAllowedRecipeHelpTooltip() {
      return Tooltip.create(
            Component.translatable(
                  "menu.building.residence.production_bills.edit_recipe.tooltip.allowed_recipe_help.toolsmith"));
   }
}
