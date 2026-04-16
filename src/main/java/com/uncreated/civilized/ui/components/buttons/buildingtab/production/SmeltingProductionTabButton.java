package com.uncreated.civilized.ui.components.buttons.buildingtab.production;

import static com.uncreated.civilized.CivilizedMod.CIVILIZED_MOD_ID;

import com.uncreated.civilized.core.building.production.bills.ProductionType;
import com.uncreated.civilized.ui.components.buttons.buildingtab.BuildingTabButton;
import com.uncreated.civilized.ui.tabs.AScreenWithTabs;

import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.resources.ResourceLocation;

public class SmeltingProductionTabButton extends BuildingTabButton {

   private static ResourceLocation ICON =
         ResourceLocation.fromNamespaceAndPath(CIVILIZED_MOD_ID, "icon/building_tab_production_smelting");

   public SmeltingProductionTabButton(AScreenWithTabs menuScreenWithTabs, int buttonTabIndex) {
      super(menuScreenWithTabs, buttonTabIndex, ICON, Tooltip.create(ProductionType.SMELTING.getHeading()));
   }
}
