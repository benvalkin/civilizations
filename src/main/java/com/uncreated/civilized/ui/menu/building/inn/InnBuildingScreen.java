package com.uncreated.civilized.ui.menu.building.inn;

import java.util.List;

import com.uncreated.civilized.ui.components.buttons.buildingtab.HomeTabButton;
import com.uncreated.civilized.ui.components.buttons.buildingtab.ManageResidentsTabButton;
import com.uncreated.civilized.ui.components.buttons.buildingtab.SettingsTabButton;
import com.uncreated.civilized.ui.context.BuildingScreenContext;
import com.uncreated.civilized.ui.menu.building.ABuildingScreen;
import com.uncreated.civilized.ui.menu.building.BuildingSettingsTab;
import com.uncreated.civilized.ui.menu.building.inn.tabs.InnMainTab;
import com.uncreated.civilized.ui.menu.building.inn.tabs.InnVisitorsTab;
import com.uncreated.civilized.ui.tabs.ATab;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;

public class InnBuildingScreen extends ABuildingScreen {

   public InnBuildingScreen(BuildingScreenContext context, Component title) {
      super(context, title);
   }

   @Override
   protected List<ATab> createTabs(int contentLeftPos, int contentTopPos, int tabWidth, int tabHeight) {

      return List.of(
            new InnMainTab(0, contentLeftPos, contentTopPos, tabWidth, tabHeight, this.font, context),
            new InnVisitorsTab(1, contentLeftPos, contentTopPos, tabWidth, tabHeight, this.font, context),
            new BuildingSettingsTab(2, contentLeftPos, contentTopPos, tabWidth, tabHeight, this.font, context));
   }

   @Override
   public List<Button> createTabButtons() {
      return List.of(
            new HomeTabButton(this, 0),
            new ManageResidentsTabButton(
                  this,
                  1,
                  Tooltip.create(Component.translatable("menu.building.inn.visitors.count.heading"))),
            new SettingsTabButton(this, 2));
   }
}
