package com.uncreated.civilized.ui.menu.building.tradingpost.tabs;

import java.util.List;

import com.uncreated.civilized.core.building.Building;
import com.uncreated.civilized.core.building.util.BuildingUtil;
import com.uncreated.civilized.core.settlement.Settlement;
import com.uncreated.civilized.core.villagerinfo.ClientVillagerStore;
import com.uncreated.civilized.core.villagerinfo.VillagerInfo;
import com.uncreated.civilized.ui.menu.building.ABuildingScreenTab;
import com.uncreated.civilized.ui.style.Colors;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.network.chat.Component;

public class TradingPostMainTab extends ABuildingScreenTab {

   private List<VillagerInfo> visitors;

   public TradingPostMainTab(
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
            settlement.displayNameTranslationExtended()
                  .withColor(Colors.SETTLEMENT_NAME)
                  .withStyle(ChatFormatting.ITALIC),
            building,
            settlement);

      visitors = createVisitorsList();
   }

   @Override
   public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
      super.renderWidget(graphics, mouseX, mouseY, partialTicks);
      graphics.drawString(font, Component.translatable("menu.building.trading_post.visitors.count", visitors.size()), getX(), getY() + 20, Colors.MENU_TEXT_DARK, false);

      for (int i = 0; i < visitors.size(); i++) {

         VillagerInfo occupant = visitors.get(i);
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

   private List<VillagerInfo> createVisitorsList() {
      return BuildingUtil.getOccupants(building, ClientVillagerStore.INSTANCE);
   }

   @Override
   public void refresh() {
      visitors = createVisitorsList();
   }
}
