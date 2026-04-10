package com.uncreated.civilized.ui.menu.building.residence.artisan.singleitem;

import static com.uncreated.civilized.CivilizedMod.CIVILIZED_MOD_ID;

import com.uncreated.civilized.ui.menu.building.residence.artisan.EditRecipeScreen;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class EditSmokingRecipeScreen extends EditRecipeScreen<EditSmokingRecipeMenu> {

   private static final ResourceLocation MENU_TEXTURE =
         ResourceLocation
               .fromNamespaceAndPath(CIVILIZED_MOD_ID, "textures/gui/container/artisan_house_edit_smoking_recipe.png");

   public EditSmokingRecipeScreen(EditSmokingRecipeMenu menu, Inventory playerInventory, Component title) {
      super(MENU_TEXTURE, menu, playerInventory, title);
   }
}
