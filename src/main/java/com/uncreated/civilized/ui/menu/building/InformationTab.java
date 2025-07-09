package com.uncreated.civilized.ui.menu.building;

import java.util.List;

import com.uncreated.civilized.core.building.Building;
import com.uncreated.civilized.core.building.util.BuildingUtil;
import com.uncreated.civilized.core.settlement.Settlement;
import com.uncreated.civilized.core.villagerinfo.ClientVillagerStore;
import com.uncreated.civilized.core.villagerinfo.VillagerInfo;
import com.uncreated.civilized.ui.style.Colors;
import com.uncreated.civilized.ui.tabs.ATab;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class InformationTab extends ATab {

   private final Building building;
   private final Settlement settlement;

   private List<VillagerInfo> occupants;

   public InformationTab(
         int index,
         int x,
         int y,
         int width,
         int height,
         Font font,
         Building building,
         Settlement settlement) {
      super(index, x, y, width, height, font);
      this.building = building;
      this.settlement = settlement;
      this.occupants = createOccupantsList();
   }

   @Override
   public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float idkSomeNumber) {

      MutableComponent information = Component.literal("Information");
      graphics.drawString(font, information, getX() + (width - font.width(information)) / 2, getY(), Colors.MENU_TEXT_DARK, false);

      graphics.drawString(font, Component.literal("Residents:"), getX(), getY() + 20, Colors.MENU_TEXT_DARK, false);

      // todo: don't do this every frame
      List<VillagerInfo> occupants = BuildingUtil.getOccupants(building, ClientVillagerStore.INSTANCE);
      for (int i = 0; i < occupants.size(); i++) {

         VillagerInfo occupant = occupants.get(i);
         graphics.drawString(
               font,
               Component.literal(occupant.getFullName()),
               getX() + 8,
               getY() + 35 + i * 10,
               Colors.MENU_TEXT_DARK,
               false);
      }
   }

   @Override
   public List<? extends GuiEventListener> children() {
      return List.of();
   }

   private List<VillagerInfo> createOccupantsList() {
      return BuildingUtil.getOccupants(building, ClientVillagerStore.INSTANCE);
   }

   @Override
   public void refresh() {
      this.occupants = createOccupantsList();
   }
}
