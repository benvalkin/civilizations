package com.uncreated.civilized.ui.menu.building;

import com.uncreated.civilized.core.building.Building;
import com.uncreated.civilized.core.settlement.Settlement;
import com.uncreated.civilized.ui.style.Colors;
import com.uncreated.civilized.ui.tabs.ATab;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public abstract class ABuildingScreenTab extends ATab {

   private final Component tabTitle;
   protected final Building building;
   protected final Settlement settlement;

   public ABuildingScreenTab(
         int index,
         int x,
         int y,
         int width,
         int height,
         Font font,
         Component tabTitle,
         Building building,
         Settlement settlement) {
      super(index, x, y, width, height, font);
      this.tabTitle = tabTitle;
      this.building = building;
      this.settlement = settlement;
   }

   @Override
   public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
      graphics.drawWordWrap(
            font,
            tabTitle,
            getX() + (width - font.width(tabTitle)) / 2,
            getY(),
            width,
            Colors.MENU_TEXT_DARK,
            false);
   }

}
