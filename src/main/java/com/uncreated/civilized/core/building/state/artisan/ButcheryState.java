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

public class ButcheryState extends ArtisanHouseState {
   public ButcheryState(Building building) {
      super(building);
   }

   @Override
   protected List<ProductionType> getSupportedProductionTypes() {
      return List.of(ProductionType.CRAFTING, ProductionType.SMOKING);
   }

   @Override
   protected List<ProductionBill> getDefaultProductionBills(ProductionType productionType) {
      return switch (productionType) {
      case CRAFTING -> List.of(
            createDefaultBill(
                  "minecraft:rabbit_stew",
                  ProductionType.CRAFTING,
                  List.of(
                        new ItemStack(Items.COOKED_RABBIT),
                        new ItemStack(Items.BAKED_POTATO),
                        new ItemStack(Items.CARROT),
                        new ItemStack(Items.BROWN_MUSHROOM),
                        new ItemStack(Items.BOWL)),
                  new ItemStack(Items.RABBIT_STEW)));
      case SMOKING -> List.of(
            createDefaultBill(
                  "minecraft:cooked_beef",
                  ProductionType.SMOKING,
                  List.of(new ItemStack(Items.BEEF)),
                  new ItemStack(Items.COOKED_BEEF)),
            createDefaultBill(
                  "minecraft:cooked_porkchop",
                  ProductionType.SMOKING,
                  List.of(new ItemStack(Items.PORKCHOP)),
                  new ItemStack(Items.COOKED_PORKCHOP)));
      default -> List.of();
      };
   }

   private static final List<TagKey<Item>> ALLOWED_INGREDIENT_TAGS =
         List.of(Tags.Items.FOODS_RAW_MEAT, Tags.Items.FOODS_COOKED_MEAT);

   @Override
   public boolean recipeAllowed(
         ProductionType productionType,
         RecipeInput recipeInput,
         ItemStack resultItem,
         ServerLevel level) {

      if (productionType == ProductionType.SMOKING)
         return true;

      return recipeHasAtLeastOneIngredientWithMatchingTag(recipeInput, ALLOWED_INGREDIENT_TAGS);
   }

   @Override
   public Tooltip getAllowedRecipeHelpTooltip() {
      return Tooltip.create(
            Component.translatable(
                  "menu.building.residence.production_bills.edit_recipe.tooltip.allowed_recipe_help.butcher"));
   }
}
