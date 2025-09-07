package com.uncreated.civilized.ui.menu.building.residence;

import java.util.List;

import com.uncreated.civilized.core.building.Building;
import com.uncreated.civilized.core.settlement.Settlement;
import com.uncreated.civilized.ui.menu.building.ABuildingScreen;
import com.uncreated.civilized.ui.menu.building.BuildingSettingsTab;
import com.uncreated.civilized.ui.menu.building.residence.tabs.ManageResidentsTab;
import com.uncreated.civilized.ui.menu.building.residence.tabs.ResidenceInfoTab;
import com.uncreated.civilized.ui.tabs.ATab;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;

public class ResidenceBuildingScreen extends ABuildingScreen {

   private Button tab1;
   private Button tab2;
   private Button tab3;

   public ResidenceBuildingScreen(Building building, Settlement settlement, Component title) {
      super(building, settlement, title);
   }

   @Override
   protected List<ATab> createTabs(int contentLeftPos, int contentTopPos, int tabWidth, int tabHeight) {

      return List.of(
            new ResidenceInfoTab(
                  0,
                  contentLeftPos,
                  contentTopPos,
                  tabWidth,
                  tabHeight,
                  this.font,
                  building,
                  settlement),
            new ManageResidentsTab(
                  1,
                  contentLeftPos,
                  contentTopPos,
                  tabWidth,
                  tabHeight,
                  this.font,
                  building,
                  settlement),
            new BuildingSettingsTab(
                  2,
                  contentLeftPos,
                  contentTopPos,
                  tabWidth,
                  tabHeight,
                  this.font,
                  building,
                  settlement));
   }

   @Override
   public List<Button.Builder> createTabButtons() {

      return List.of(
            Button.builder(Component.literal("I"), this::onClickTab1)
                  .pos(leftPos - 18, topPos)
                  .size(18, 18)
                  .tooltip(Tooltip.create(Component.translatable("menu.building.residence.info.tab.heading"))),

            Button.builder(Component.literal("R"), this::onClickTab2)
                  .pos(leftPos - 18, topPos + 20)
                  .size(18, 18)
                  .tooltip(Tooltip.create(Component.translatable("menu.building.residence.residents.tab.heading"))),

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
