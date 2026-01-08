package com.uncreated.civilized.ui.menu.building;

import com.uncreated.civilized.core.building.Building;
import com.uncreated.civilized.core.settlement.Settlement;
import com.uncreated.civilized.ui.context.BuildingScreenContext;
import com.uncreated.civilized.ui.style.Colors;
import com.uncreated.civilized.ui.tabs.ATabWithViewableItemSlots;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public abstract class ABuildingScreenTab extends ATabWithViewableItemSlots {

   private final Component tabTitle;
   protected final BuildingScreenContext context;

   public ABuildingScreenTab(
         int index,
         int x,
         int y,
         int width,
         int height,
         Font font,
         Component tabTitle,
         BuildingScreenContext context) {
      super(index, x, y, width, height, font);
      this.tabTitle = tabTitle;
      this.context = context;
   }

   @Override
   public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
      int fontStartX = (width - font.width(tabTitle)) / 2;
      graphics.drawWordWrap(
            font,
            tabTitle,
            getX() + fontStartX,
            getY(),
            width - fontStartX,
            Colors.MENU_TEXT_DARK,
            false);
   }

}
