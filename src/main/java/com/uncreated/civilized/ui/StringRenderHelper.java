package com.uncreated.civilized.ui;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

public class StringRenderHelper {
   public static int drawCenterAlignedWordWrap(
         GuiGraphics guiGraphics,
         Font font,
         Component message,
         int x,
         int y,
         int maxWidth,
         int color,
         boolean dropShadow) {
      int numberOfWraps = 0;
      for (FormattedCharSequence formattedcharsequence : font.split(message, maxWidth)) {
         drawCenteredString(
               guiGraphics,
               font,
               formattedcharsequence,
               x,
               y + numberOfWraps * font.lineHeight,
               color,
               dropShadow);
         numberOfWraps++;
      }
      return numberOfWraps * font.lineHeight;
   }
   public static int getHeightOfWrappedText(Font font, Component message, int maxWidth) {
      return font.split(message, maxWidth).size() * font.lineHeight;
   }

   public static void drawCenteredString(
         GuiGraphics guiGraphics,
         Font font,
         FormattedCharSequence text,
         int x,
         int y,
         int color,
         boolean dropShadow) {
      guiGraphics.drawString(font, text, x - font.width(text) / 2, y, color, dropShadow);
   }

   public static void drawCenteredString(
         GuiGraphics guiGraphics,
         Font font,
         Component text,
         int x,
         int y,
         int color,
         boolean dropShadow) {
      FormattedCharSequence formattedcharsequence = text.getVisualOrderText();
      guiGraphics
            .drawString(font, formattedcharsequence, x - font.width(formattedcharsequence) / 2, y, color, dropShadow);
   }
}
