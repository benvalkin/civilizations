package com.uncreated.civilized.neoforge.registration.gui;

import com.uncreated.civilized.ui.menu.building.worksite.animalfarm.items.ChooseAnimalFoodMenu;
import com.uncreated.civilized.ui.menu.building.worksite.animalfarm.items.ChooseAnimalFoodScreen;
import com.uncreated.civilized.ui.menu.building.worksite.cropfarm.items.ChooseCropsMenu;
import com.uncreated.civilized.ui.menu.building.worksite.cropfarm.items.ChooseCropsScreen;
import com.uncreated.civilized.ui.menu.building.worksite.grove.items.ChooseSaplingsMenu;
import com.uncreated.civilized.ui.menu.building.worksite.grove.items.ChooseSaplingsScreen;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.network.chat.Component;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

public class GuiSetupEvents {
   @SubscribeEvent
   public static void registerScreens(RegisterMenuScreensEvent event) {
      event.register(
            GuiRegistry.CHOOSE_CROPS_MENU.get(), // do not remove cast - it seems to cause compile errors even though
            // intellij thinks its redundant
            (MenuScreens.ScreenConstructor<ChooseCropsMenu, ChooseCropsScreen>) (
                  buildingMenu,
                  inventory,
                  component) -> new ChooseCropsScreen(
                        buildingMenu,
                        inventory,
                        Component.translatable("menu.building.worksite.crop_farm.allowed_crops.description")));

      event.register(
            GuiRegistry.CHOOSE_SAPLINGS_MENU.get(), // do not remove cast - it seems to cause compile errors even though
            // intellij thinks its redundant
            (MenuScreens.ScreenConstructor<ChooseSaplingsMenu, ChooseSaplingsScreen>) (
                  buildingMenu,
                  inventory,
                  component) -> new ChooseSaplingsScreen(
                        buildingMenu,
                        inventory,
                        Component.translatable("menu.building.worksite.grove.allowed_saplings.description")));

      event.register(
            GuiRegistry.CHOOSE_ANIMAL_FOOD_MENU.get(), // do not remove cast - it seems to cause compile errors even
                                                       // though
            // intellij thinks its redundant
            (MenuScreens.ScreenConstructor<ChooseAnimalFoodMenu, ChooseAnimalFoodScreen>) (
                  buildingMenu,
                  inventory,
                  component) -> new ChooseAnimalFoodScreen(
                        buildingMenu,
                        inventory,
                        Component.translatable("menu.building.worksite.animal_farm.allowed_animal_food.description")));
   }
}
