package com.uncreated.civilized.ui.menu.building.worksite.residence;

import java.util.List;

import com.uncreated.civilized.ui.menu.building.ABuildingScreen;
import com.uncreated.civilized.ui.menu.building.BuildingMenu;
import com.uncreated.civilized.ui.menu.building.BuildingSettingsTab;
import com.uncreated.civilized.ui.menu.building.worksite.residence.tabs.ManageWorkersTab;
import com.uncreated.civilized.ui.menu.building.worksite.residence.tabs.WorksiteInfoTab;
import com.uncreated.civilized.ui.tabs.ATab;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class WorksiteBuildingScreen extends ABuildingScreen {

   private Button tab1;
   private Button tab2;
   private Button tab3;

   private BuildingMenu menu;

   public WorksiteBuildingScreen(BuildingMenu menu, Inventory playerInventory, Component title) {
      super(menu, playerInventory, title);
      this.menu = menu;
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
                  menu.getBuilding(),
                  menu.getSettlement()),
            new ManageWorkersTab(
                  1,
                  contentLeftPos,
                  contentTopPos,
                  tabWidth,
                  tabHeight,
                  this.font,
                  menu.getBuilding(),
                  menu.getSettlement()),
            new BuildingSettingsTab(
                  2,
                  contentLeftPos,
                  contentTopPos,
                  tabWidth,
                  tabHeight,
                  this.font,
                  menu.getBuilding(),
                  menu.getSettlement()));
   }

   @Override
   public List<Button.Builder> createTabButtons() {

      return List.of(
            Button.builder(Component.literal("I"), this::onClickTab1)
                  .pos(leftPos + 60, topPos + 120)
                  .size(18, 18)
                  .tooltip(Tooltip.create(Component.translatable("menu.building.worksite.info.tab.heading"))),

            Button.builder(Component.literal("W"), this::onClickTab2)
                  .pos(leftPos + 60, topPos + 140)
                  .size(18, 18)
                  .tooltip(Tooltip.create(Component.translatable("menu.building.worksite.workers.tab.heading"))),

            Button.builder(Component.literal("S"), this::onClickTab3)
                  .pos(leftPos + 60, topPos + 160)
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
