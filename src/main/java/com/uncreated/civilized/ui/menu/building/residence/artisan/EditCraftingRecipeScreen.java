package com.uncreated.civilized.ui.menu.building.residence.artisan;

import static com.uncreated.civilized.CivilizedMod.CIVILIZED_MOD_ID;

import com.uncreated.civilized.ui.menu.building.item.management.ItemManagementScreen;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class EditCraftingRecipeScreen extends ItemManagementScreen<EditCraftingRecipeMenu> {
   private static final ResourceLocation MENU_TEXTURE =
         ResourceLocation
               .fromNamespaceAndPath(CIVILIZED_MOD_ID, "textures/gui/container/artisan_house_edit_crafting_recipe.png");

   public EditCraftingRecipeScreen(EditCraftingRecipeMenu menu, Inventory playerInventory, Component title) {
      super(MENU_TEXTURE, menu, playerInventory, title);
   }
}
