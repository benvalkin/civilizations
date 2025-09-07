package com.uncreated.civilized.ui.menu.building.worksite.grove.items;

import static com.uncreated.civilized.CivilizedMod.CIVILIZED_MOD_ID;

import com.uncreated.civilized.ui.menu.building.item.management.ItemManagementScreen;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class ChooseSaplingsScreen extends ItemManagementScreen<ChooseSaplingsMenu> {
   private static final ResourceLocation MENU_TEXTURE =
         ResourceLocation.fromNamespaceAndPath(CIVILIZED_MOD_ID, "textures/gui/container/grove_choose_saplings.png");

   public ChooseSaplingsScreen(ChooseSaplingsMenu menu, Inventory playerInventory, Component title) {
      super(MENU_TEXTURE, menu, playerInventory, title);
   }
}
