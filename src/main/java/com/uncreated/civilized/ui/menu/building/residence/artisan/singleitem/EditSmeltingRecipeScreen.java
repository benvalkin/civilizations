package com.uncreated.civilized.ui.menu.building.residence.artisan.singleitem;

import static com.uncreated.civilized.CivilizedMod.CIVILIZED_MOD_ID;

import com.mojang.datafixers.util.Pair;
import com.uncreated.civilized.ui.menu.building.residence.artisan.EditRecipeScreen;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class EditSmeltingRecipeScreen extends EditRecipeScreen<EditSmeltingRecipeMenu> {

   private static final ResourceLocation MENU_TEXTURE =
         ResourceLocation
               .fromNamespaceAndPath(CIVILIZED_MOD_ID, "textures/gui/container/artisan_house_edit_cooking_recipe.png");

   public EditSmeltingRecipeScreen(EditSmeltingRecipeMenu menu, Inventory playerInventory, Component title) {
      super(MENU_TEXTURE, menu, playerInventory, title);
   }

   protected Pair<Integer, Integer> getRecipeAllowedIconXYOffset() {
      return Pair.of(67, 22);
   }
}
