package com.uncreated.civilized.ui.menu.building.residence.artisan;

import java.util.List;

import com.uncreated.civilized.core.building.production.bills.ProductionTypes;
import com.uncreated.civilized.ui.components.buttons.buildingtab.HomeTabButton;
import com.uncreated.civilized.ui.components.buttons.buildingtab.ManageResidentsTabButton;
import com.uncreated.civilized.ui.components.buttons.buildingtab.SettingsTabButton;
import com.uncreated.civilized.ui.components.buttons.buildingtab.production.CraftingProductionTabButton;
import com.uncreated.civilized.ui.components.buttons.buildingtab.production.SmeltingProductionTabButton;
import com.uncreated.civilized.ui.context.BuildingScreenContext;
import com.uncreated.civilized.ui.menu.building.ABuildingScreen;
import com.uncreated.civilized.ui.menu.building.BuildingSettingsTab;
import com.uncreated.civilized.ui.menu.building.residence.tabs.ManageResidentsTab;
import com.uncreated.civilized.ui.menu.building.residence.tabs.ResidenceInfoTab;
import com.uncreated.civilized.ui.tabs.ATab;

import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

public class BlacksmithBuildingScreen extends ABuildingScreen {

   public BlacksmithBuildingScreen(BuildingScreenContext context, Component title) {
      super(context, title);
   }

   @Override
   protected List<ATab> createTabs(int contentLeftPos, int contentTopPos, int tabWidth, int tabHeight) {

      return List.of(
            new ResidenceInfoTab(0, contentLeftPos, contentTopPos, tabWidth, tabHeight, this.font, context),
            new ManageProductionBillsTab(
                  1,
                  contentLeftPos,
                  contentTopPos,
                  tabWidth,
                  tabHeight,
                  this.font,
                  context,
                  ProductionTypes.CRAFTING),
            new ManageProductionBillsTab(
                  2,
                  contentLeftPos,
                  contentTopPos,
                  tabWidth,
                  tabHeight,
                  this.font,
                  context,
                  ProductionTypes.SMELTING),
            new ManageResidentsTab(3, contentLeftPos, contentTopPos, tabWidth, tabHeight, this.font, context),
            new BuildingSettingsTab(4, contentLeftPos, contentTopPos, tabWidth, tabHeight, this.font, context));
   }

   @Override
   public List<Button> createTabButtons() {
      return List.of(
            new HomeTabButton(this, 0),
            new CraftingProductionTabButton(this, 1),
            new SmeltingProductionTabButton(this, 2),
            new ManageResidentsTabButton(this, 3),
            new SettingsTabButton(this, 4));
   }
}
