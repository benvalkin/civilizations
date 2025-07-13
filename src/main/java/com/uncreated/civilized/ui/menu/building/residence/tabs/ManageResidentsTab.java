package com.uncreated.civilized.ui.menu.building.residence.tabs;

import java.util.List;

import org.apache.commons.compress.utils.Lists;

import com.uncreated.civilized.core.building.Building;
import com.uncreated.civilized.core.building.util.BuildingUtil;
import com.uncreated.civilized.core.settlement.Settlement;
import com.uncreated.civilized.core.settlement.util.SettlementUtil;
import com.uncreated.civilized.core.villagerinfo.ClientVillagerStore;
import com.uncreated.civilized.core.villagerinfo.VillagerInfo;
import com.uncreated.civilized.entity.VillagerOccupation;
import com.uncreated.civilized.ui.components.ScrollListView;
import com.uncreated.civilized.ui.menu.building.ABuildingScreenTab;
import com.uncreated.civilized.ui.menu.building.widgets.ManageOccupantWidget;
import com.uncreated.civilized.ui.style.Colors;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.network.chat.Component;

public class ManageResidentsTab extends ABuildingScreenTab {

   private ScrollListView scrollView;
   private List<VillagerInfo> residents;

   public ManageResidentsTab(
         int index,
         int x,
         int y,
         int width,
         int height,
         Font font,
         Building building,
         Settlement settlement) {
      super(
            index,
            x,
            y,
            width,
            height,
            font,
            Component.translatable("menu.building.residence.residents.tab.heading"),
            building,
            settlement);
      residents = BuildingUtil.getOccupants(building, ClientVillagerStore.INSTANCE);
      scrollView = createScrollView(residents);
   }

   @Override
   public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
      super.renderWidget(graphics, mouseX, mouseY, partialTicks);

      if (residents.isEmpty()) {
         graphics.drawString(
               font,
               Component.translatable("menu.building.residence.residents.count.heading.empty"),
               getX(),
               getY() + 20,
               Colors.MENU_TEXT_DARK,
               false);
         return;
      }

      scrollView.render(graphics, mouseX, mouseY, partialTicks);

      // List<CivilizedVillager> occupants = getBuilding.get().getOccupants();
      // for (int i = 0; i < occupants.size(); i++) {
      // CivilizedVillager occupant = occupants.get(i);
      // graphics.drawString(
      // parent.getFont(),
      // Component.literal(occupant.getFirstName() + " " + occupant.getLastName()),
      // parent.getContentLeftPos() + 4,
      // parent.getContentTopPos() + 35 + i * 10,
      // Colors.MENU_TEXT_DARK,
      // false);
      // }
   }

   @Override
   public List<? extends GuiEventListener> children() {
      List<GuiEventListener> children = Lists.newArrayList();
      children.add(scrollView);
      children.addAll(scrollView.children());
      return children;
   }

   private ScrollListView createScrollView(List<VillagerInfo> residents) {
      return new ScrollListView(getX(), getY() + 15, width, height, (x_, y_, w, h) -> {
         List<AbstractWidget> elements = Lists.newArrayList();
         List<VillagerInfo> citizens =
               SettlementUtil.getCitizens(settlement, ClientVillagerStore.INSTANCE).stream().sorted((v1, v2) -> {

                  boolean v1IsOccupant = building.getBuildingId().equals(v1.getHomeBuildingId());
                  boolean v2IsOccupant = building.getBuildingId().equals(v2.getHomeBuildingId());
                  if (v1IsOccupant && v2IsOccupant)
                     return 0;
                  else if (v1IsOccupant)
                     return -1;
                  else
                     return 1;
               }).toList();

         final int elementHeight = 25;

         int elementIndex = 0;
         for (VillagerInfo villager : citizens) {

            // Building home = ClientBuildingStore.INSTANCE.get(villager.getHomeBuildingId());
            boolean isUnemployed = villager.getOccupation() == VillagerOccupation.UNEMPLOYED;
            boolean isBuildingFull = residents.size() >= 2;
            // boolean isOccupantOfAnotherBuilding = !isOccupantOfThisBuilding && villager.getHomeBuildingId() != null;

            ManageOccupantWidget.EManagementOption mode;
            if (villager.isOccupantOf(building))
               mode = ManageOccupantWidget.EManagementOption.EVICT;
            else if (isUnemployed)
               mode = ManageOccupantWidget.EManagementOption.ASSIGN;
            else
               continue;

            elements.add(
                  new ManageOccupantWidget(
                        x_,
                        y_ + elementIndex * elementHeight,
                        w - 10,
                        elementHeight,
                        font,
                        building,
                        villager,
                        mode,
                        isBuildingFull));
            elementIndex++;
         }

         return elements;
      });
   }

   @Override
   public void refresh() {
      residents = BuildingUtil.getOccupants(building, ClientVillagerStore.INSTANCE);
      scrollView = createScrollView(residents);
   }
}
