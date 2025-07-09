package com.uncreated.civilized.neoforge.registration.gui;

import com.uncreated.civilized.ui.menu.building.BuildingScreen;
import com.uncreated.civilized.ui.TestContainerScreen;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

public class GuiSetupEvents {
   @SubscribeEvent
   public static void registerScreens(RegisterMenuScreensEvent event) {
      event.register(GuiRegistry.TEST_CONTAINER_MENU.get(), TestContainerScreen::new);
      event.register(GuiRegistry.BUILDING_MENU.get(), BuildingScreen::new);
   }
}
