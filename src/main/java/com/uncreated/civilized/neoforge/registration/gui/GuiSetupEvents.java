package com.uncreated.civilized.neoforge.registration.gui;

import com.uncreated.civilized.core.building.BuildingType;
import com.uncreated.civilized.ui.menu.building.ABuildingScreen;
import com.uncreated.civilized.ui.menu.building.BuildingMenu;
import com.uncreated.civilized.ui.menu.building.residence.ResidenceBuildingScreen;
import com.uncreated.civilized.ui.menu.building.tradingpost.TradingPostBuildingScreen;

import net.minecraft.client.gui.screens.MenuScreens;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

public class GuiSetupEvents {
   @SubscribeEvent
   public static void registerScreens(RegisterMenuScreensEvent event) {
      event.register(
            GuiRegistry.BUILDING_MENU.get(), // do not remove cast - it seems to cause compile errors even though
                                             // intellij thinks its redundant
            (MenuScreens.ScreenConstructor<BuildingMenu, ABuildingScreen>) (buildingMenu, inventory, component) -> {
               BuildingType buildingType = buildingMenu.getBuilding().getBuildingType();

               if (buildingType == BuildingType.TRADING_POST)
                  return new TradingPostBuildingScreen(buildingMenu, inventory, component);
               if (buildingType.isPermanentResidence())
                  return new ResidenceBuildingScreen(buildingMenu, inventory, component);

               return new ResidenceBuildingScreen(buildingMenu, inventory, component);
            });
   }
}
