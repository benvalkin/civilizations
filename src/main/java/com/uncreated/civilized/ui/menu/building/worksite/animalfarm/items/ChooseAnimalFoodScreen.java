package com.uncreated.civilized.ui.menu.building.worksite.animalfarm.items;

import static com.uncreated.civilized.CivilizedMod.CIVILIZED_MOD_ID;

import com.uncreated.civilized.ui.menu.building.item.management.ItemManagementScreen;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class ChooseAnimalFoodScreen extends ItemManagementScreen<ChooseAnimalFoodMenu> {
   private static final ResourceLocation MENU_TEXTURE =
         ResourceLocation
               .fromNamespaceAndPath(CIVILIZED_MOD_ID, "textures/gui/container/animal_farm_choose_animal_food.png");

   public ChooseAnimalFoodScreen(ChooseAnimalFoodMenu menu, Inventory playerInventory, Component title) {
      super(MENU_TEXTURE, menu, playerInventory, title);
   }
}
