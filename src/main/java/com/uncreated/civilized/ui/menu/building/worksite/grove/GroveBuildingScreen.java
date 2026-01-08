package com.uncreated.civilized.ui.menu.building.worksite.grove;

import java.util.List;

import com.uncreated.civilized.ui.context.BuildingScreenContext;
import com.uncreated.civilized.ui.menu.building.BuildingSettingsTab;
import com.uncreated.civilized.ui.menu.building.worksite.WorksiteBuildingScreen;
import com.uncreated.civilized.ui.menu.building.worksite.grove.tabs.GroveInfoTab;
import com.uncreated.civilized.ui.menu.building.worksite.tabs.ManageWorkersTab;
import com.uncreated.civilized.ui.tabs.ATab;

import net.minecraft.network.chat.Component;

public class GroveBuildingScreen extends WorksiteBuildingScreen {

   public GroveBuildingScreen(BuildingScreenContext context, Component title) {
      super(context, title);
   }

   @Override
   protected List<ATab> createTabs(int contentLeftPos, int contentTopPos, int tabWidth, int tabHeight) {

      return List.of(
            new GroveInfoTab(0, contentLeftPos, contentTopPos, tabWidth, tabHeight, this.font, context),
            new ManageWorkersTab(1, contentLeftPos, contentTopPos, tabWidth, tabHeight, this.font, context),
            new BuildingSettingsTab(2, contentLeftPos, contentTopPos, tabWidth, tabHeight, this.font, context));
   }
}
