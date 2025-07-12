package com.uncreated.civilized.ui.menu.building.tradingpost;

import java.util.List;

import com.uncreated.civilized.ui.menu.building.ABuildingScreen;
import com.uncreated.civilized.ui.menu.building.BuildingMenu;
import com.uncreated.civilized.ui.menu.building.residence.tabs.ManageResidentsTab;
import com.uncreated.civilized.ui.menu.building.residence.tabs.ResidenceInfoTab;
import com.uncreated.civilized.ui.menu.building.tradingpost.tabs.TradingPostMainTab;
import com.uncreated.civilized.ui.menu.building.tradingpost.tabs.TradingPostVisitorsTab;
import com.uncreated.civilized.ui.tabs.ATab;
import com.uncreated.civilized.ui.tabs.BlankTab;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class TradingPostBuildingScreen extends ABuildingScreen {

   private Button tab1;
   private Button tab2;
   private Button tab3;

   private BuildingMenu menu;

   public TradingPostBuildingScreen(BuildingMenu menu, Inventory playerInventory, Component title) {
      super(menu, playerInventory, title);
      this.menu = menu;
   }

   @Override
   protected List<ATab> createTabs(int contentLeftPos, int contentTopPos, int tabWidth, int tabHeight) {

      return List.of(
            new TradingPostMainTab(
                  0,
                  contentLeftPos,
                  contentTopPos,
                  tabWidth,
                  tabHeight,
                  this.font,
                  menu.getBuilding(),
                  menu.getSettlement()),
            new TradingPostVisitorsTab(
                  1,
                  contentLeftPos,
                  contentTopPos,
                  tabWidth,
                  tabHeight,
                  this.font,
                  menu.getBuilding(),
                  menu.getSettlement()),
            new BlankTab(2, contentLeftPos, contentTopPos, tabWidth, tabHeight, this.font, "This is Tab #3 :)"));
   }

   @Override
   public List<Button.Builder> createTabButtons() {

      return List.of(
            Button.builder(Component.literal("I"), this::onClickTab1)
                  .pos(leftPos + 20, topPos + 20)
                  .size(18, 18)
                  .tooltip(Tooltip.create(Component.literal("Information"))),

            Button.builder(Component.literal("R"), this::onClickTab2)
                  .pos(leftPos + 20, topPos + 40)
                  .size(18, 18)
                  .tooltip(Tooltip.create(Component.literal("Visitors"))),

            Button.builder(Component.literal("O"), this::onClickTab3)
                  .pos(leftPos + 20, topPos + 60)
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
