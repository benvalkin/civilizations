package com.uncreated.civilized.ui.menu.building.inn;

import java.util.List;

import com.uncreated.civilized.ui.menu.building.ABuildingScreen;
import com.uncreated.civilized.ui.menu.building.BuildingMenu;
import com.uncreated.civilized.ui.menu.building.BuildingSettingsTab;
import com.uncreated.civilized.ui.menu.building.inn.tabs.InnMainTab;
import com.uncreated.civilized.ui.menu.building.inn.tabs.InnVisitorsTab;
import com.uncreated.civilized.ui.tabs.ATab;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class InnBuildingScreen extends ABuildingScreen {

   private Button tab1;
   private Button tab2;
   private Button tab3;

   private BuildingMenu menu;

   public InnBuildingScreen(BuildingMenu menu, Inventory playerInventory, Component title) {
      super(menu, playerInventory, title);
      this.menu = menu;
   }

   @Override
   protected List<ATab> createTabs(int contentLeftPos, int contentTopPos, int tabWidth, int tabHeight) {

      return List.of(
            new InnMainTab(
                  0,
                  contentLeftPos,
                  contentTopPos,
                  tabWidth,
                  tabHeight,
                  this.font,
                  menu.getBuilding(),
                  menu.getSettlement()),
            new InnVisitorsTab(
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
                  .tooltip(Tooltip.create(Component.literal("Information"))),

            Button.builder(Component.literal("R"), this::onClickTab2)
                  .pos(leftPos + 60, topPos + 140)
                  .size(18, 18)
                  .tooltip(Tooltip.create(Component.literal("Visitors"))),

            Button.builder(Component.literal("O"), this::onClickTab3)
                  .pos(leftPos + 60, topPos + 160)
                  .size(18, 18)
                  .tooltip(Tooltip.create(Component.literal("Building Settings"))));
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
