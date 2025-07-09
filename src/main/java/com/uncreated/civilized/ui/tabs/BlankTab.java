package com.uncreated.civilized.ui.tabs;

import java.util.List;

import com.uncreated.civilized.ui.style.Colors;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.network.chat.Component;

public class BlankTab extends ATab {

   private final String text;

   public BlankTab(int index, int x, int y, int width, int height, Font font, String text) {
      super(index, x, y, width, height, font);
      this.text = text;
   }

   @Override
   public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float idkSomeNumber) {
      graphics.drawString(font, Component.literal(text), getX(), getY(), Colors.MENU_TEXT_DARK, false);
   }

   @Override
   public List<? extends GuiEventListener> children() {
      return List.of();
   }
}
