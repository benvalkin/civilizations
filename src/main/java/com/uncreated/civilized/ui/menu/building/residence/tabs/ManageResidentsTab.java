package com.uncreated.civilized.ui.menu.building.residence.tabs;

import java.util.List;

import org.apache.commons.compress.utils.Lists;

import com.uncreated.civilized.core.building.Building;
import com.uncreated.civilized.core.building.util.BuildingUtil;
import com.uncreated.civilized.core.settlement.Settlement;
import com.uncreated.civilized.core.settlement.util.SettlementUtil;
import com.uncreated.civilized.core.villagerinfo.ClientVillagerStore;
import com.uncreated.civilized.core.villagerinfo.VillagerInfo;
import com.uncreated.civilized.core.villagerinfo.VillagerOccupation;
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

   public static final int MAX_ASSIGNED_RESIDENTS = 2;
   private ScrollListView scrollView;
   private List<VillagerInfo> currentOccupants;
   private List<VillagerInfo> candidateOccupants;

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
      refresh();
   }

   @Override
   public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
      super.renderWidget(graphics, mouseX, mouseY, partialTicks);

      Component heading;
      if (candidateOccupants.isEmpty())
         heading = getSubHeadingNoCandidates();
      else
         heading = getSubHeading(currentOccupants);

      graphics.drawWordWrap(font, heading, getX(), getY() + 15, width, Colors.MENU_TEXT_DARK, false);

      if (!candidateOccupants.isEmpty())
         scrollView.render(graphics, mouseX, mouseY, partialTicks);
   }

   protected Component getSubHeading(List<VillagerInfo> currentOccupants) {
      return Component.translatable("menu.building.residence.residents.heading", currentOccupants.size());
   }

   protected Component getSubHeadingNoCandidates() {
      return Component.translatable("menu.building.residence.residents.heading.empty");
   }

   @Override
   public List<? extends GuiEventListener> children() {
      List<GuiEventListener> children = Lists.newArrayList();
      children.add(scrollView);
      children.addAll(scrollView.children());
      return children;
   }

   private ScrollListView createScrollView(List<VillagerInfo> currentOccupants, List<VillagerInfo> candidateOccupants) {
      return new ScrollListView(getX(), getY() + 30, width, height, (x_, y_, w, h) -> {
         List<AbstractWidget> elements = Lists.newArrayList();

         boolean isBuildingFull = currentOccupants.size() >= getMaxNumberOfOccupants();

         final int elementHeight = 25;

         int elementIndex = 0;
         for (VillagerInfo villager : candidateOccupants) {

            ManageOccupantWidget.ManagementOption mode = getAssignButtonAction(villager);

            if (mode == ManageOccupantWidget.ManagementOption.NOT_APPLICABLE)
               continue;

            elements.add(
                  createManagementWidget(
                        x_,
                        y_ + elementIndex * elementHeight,
                        w - 10,
                        elementHeight,
                        villager,
                        mode,
                        isBuildingFull));
            elementIndex++;
         }

         return elements;
      });
   }

   protected List<VillagerInfo> getCurrentOccupants(Building building, Settlement settlement) {
      return BuildingUtil.getResidents(building, ClientVillagerStore.INSTANCE);
   }

   protected List<VillagerInfo> getCandidateOccupants(Building building, Settlement settlement) {
      return ClientVillagerStore.INSTANCE.getCitizens(settlement.getSettlementId()).stream().sorted((v1, v2) -> {
         boolean v1IsOccupant = building.getBuildingId().equals(v1.getHomeBuildingId());
         boolean v2IsOccupant = building.getBuildingId().equals(v2.getHomeBuildingId());
         if (v1IsOccupant && v2IsOccupant)
            return 0;
         else if (v1IsOccupant)
            return -1;
         else
            return 1;
      }).toList();
   }

   protected int getMaxNumberOfOccupants() {
      return MAX_ASSIGNED_RESIDENTS;
   }

   protected ManageOccupantWidget.ManagementOption getAssignButtonAction(VillagerInfo villager) {
      if (villager.isOccupantOf(building))
         return ManageOccupantWidget.ManagementOption.UNASSIGN;
      else if (villager.getOccupation() == VillagerOccupation.UNEMPLOYED)
         return ManageOccupantWidget.ManagementOption.ASSIGN;

      return ManageOccupantWidget.ManagementOption.NOT_APPLICABLE;
   }

   protected ManageOccupantWidget createManagementWidget(
         int x,
         int y,
         int width,
         int height,
         VillagerInfo villagerInfo,
         ManageOccupantWidget.ManagementOption managementOption,
         boolean isBuildingFull) {
      return new ManageOccupantWidget(
            x,
            y,
            width,
            height,
            font,
            building,
            villagerInfo,
            managementOption,
            isBuildingFull);
   }

   @Override
   public void refresh() {
      currentOccupants = getCurrentOccupants(building, settlement);
      candidateOccupants = getCandidateOccupants(building, settlement);
      scrollView = createScrollView(currentOccupants, candidateOccupants);
   }
}
