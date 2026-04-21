package com.uncreated.civilized.core.building.production.bills;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.crafting.RecipeType;

public enum ProductionType {
   CRAFTING, SMELTING, BLASTING, SMOKING;

   public Component getHeading() {
      return switch (this) {
      case CRAFTING -> Component.translatable("menu.building.residence.crafting_production.tab.heading");
      case SMELTING -> Component.translatable("menu.building.residence.smelting_production.tab.heading");
      case BLASTING -> Component.translatable("menu.building.residence.blasting_production.tab.heading");
      case SMOKING -> Component.translatable("menu.building.residence.smoking_production.tab.heading");
      };
   }

   public RecipeType<?> getRecipeType() {
      return switch (this) {
      case CRAFTING -> RecipeType.CRAFTING;
      case SMELTING -> RecipeType.SMELTING;
      case BLASTING -> RecipeType.BLASTING;
      case SMOKING -> RecipeType.SMOKING;
      };
   }
}
