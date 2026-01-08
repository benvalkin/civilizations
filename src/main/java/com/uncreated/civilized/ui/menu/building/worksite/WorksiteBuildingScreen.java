package com.uncreated.civilized.ui.menu.building.worksite;

import java.util.List;

import com.uncreated.civilized.core.building.Building;
import com.uncreated.civilized.core.settlement.Settlement;
import com.uncreated.civilized.ui.context.BuildingScreenContext;
import com.uncreated.civilized.ui.menu.building.ABuildingScreen;
import com.uncreated.civilized.ui.menu.building.BuildingSettingsTab;
import com.uncreated.civilized.ui.menu.building.worksite.tabs.ManageWorkersTab;
import com.uncreated.civilized.ui.menu.building.worksite.tabs.WorksiteInfoTab;
import com.uncreated.civilized.ui.tabs.ATab;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;

public class WorksiteBuildingScreen extends ABuildingScreen {

   private Button tab1;
   private Button tab2;
   private Button tab3;

   public WorksiteBuildingScreen(
         BuildingScreenContext context,
         Component title) {
      super(context, title);
   }

   @Override
   protected List<ATab> createTabs(int contentLeftPos, int contentTopPos, int tabWidth, int tabHeight) {

      return List.of(
            new WorksiteInfoTab(
                  0,
                  contentLeftPos,
                  contentTopPos,
                  tabWidth,
                  tabHeight,
                  this.font,
                  context),
            new ManageWorkersTab(
                  1,
                  contentLeftPos,
                  contentTopPos,
                  tabWidth,
                  tabHeight,
                  this.font,
                  context),
            new BuildingSettingsTab(
                  2,
                  contentLeftPos,
                  contentTopPos,
                  tabWidth,
                  tabHeight,
                  this.font,
                  context));
   }

   @Override
   public List<Button.Builder> createTabButtons() {

      return List.of(
            Button.builder(Component.literal("I"), this::onClickTab1)
                  .pos(leftPos - 18, topPos)
                  .size(18, 18)
                  .tooltip(Tooltip.create(Component.translatable("menu.building.worksite.info.tab.heading"))),

            Button.builder(Component.literal("W"), this::onClickTab2)
                  .pos(leftPos - 18, topPos + 20)
                  .size(18, 18)
                  .tooltip(Tooltip.create(Component.translatable("menu.building.worksite.workers.tab.heading"))),

            Button.builder(Component.literal("S"), this::onClickTab3)
                  .pos(leftPos - 18, topPos + 40)
                  .size(18, 18)
                  .tooltip(Tooltip.create(Component.translatable("menu.building.settings.tab.heading"))));
   }

   private void onClickTab1(Button button) {
      changeTab(0);
   }

   private void onClickTab2(Button button) {
      changeTab(1);
   }

   private void onClickTab3(Button button) {
      changeTab(2);
   }
}
