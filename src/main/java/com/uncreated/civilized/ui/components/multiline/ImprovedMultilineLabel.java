package com.uncreated.civilized.ui.components.multiline;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

public abstract class ImprovedMultilineLabel implements MultiLineLabel {

   static final ImprovedMultilineLabel EMPTY = new ImprovedMultilineLabel() {
      public void renderCentered(GuiGraphics p_283287_, int p_94383_, int p_94384_) {
      }

      public void renderCentered(GuiGraphics p_283208_, int p_210825_, int p_210826_, int p_210827_, int p_210828_) {
      }

      public void renderLeftAligned(GuiGraphics p_283077_, int p_94379_, int p_94380_, int p_282157_, int p_282742_) {
      }

      public int renderLeftAlignedNoShadow(
            GuiGraphics p_283645_,
            int p_94389_,
            int p_94390_,
            int p_94391_,
            int p_94392_) {
         return p_94390_;
      }

      public int getLineCount() {
         return 0;
      }

      public int getWidth() {
         return 0;
      }
   };

   static ImprovedMultilineLabel create(Font font, boolean dropShadows, Component... components) {
      return create(font, Integer.MAX_VALUE, Integer.MAX_VALUE, dropShadows, components);
   }

   static ImprovedMultilineLabel create(Font font, int maxWidth, boolean dropShadows, Component... components) {
      return create(font, maxWidth, Integer.MAX_VALUE, dropShadows, components);
   }

   static ImprovedMultilineLabel create(Font font, Component component, int maxWidth, boolean dropShadows) {
      return create(font, maxWidth, Integer.MAX_VALUE, dropShadows, component);
   }

   static ImprovedMultilineLabel create(
         final Font font,
         final int maxWidth,
         final int maxRows,
         boolean dropShadows,
         final Component... components) {
      return components.length == 0 ? EMPTY : new ImprovedMultilineLabel() {
         @Nullable
         private List<MultiLineLabel.TextAndWidth> cachedTextAndWidth;
         @Nullable
         private Language splitWithLanguage;

         public void renderCentered(GuiGraphics p_281603_, int p_281267_, int p_281819_) {
            this.renderCentered(p_281603_, p_281267_, p_281819_, 9, -1);
         }

         public void renderCentered(GuiGraphics graphics, int p_283184_, int p_282078_, int p_352944_, int p_352919_) {
            int i = p_282078_;

            for (TextAndWidth multilinelabel$textandwidth : this.getSplitMessage()) {
               drawCenteredString(graphics, font, multilinelabel$textandwidth.text(), p_283184_, i, p_352919_, dropShadows);
               i += p_352944_;
            }

         }

         public void renderLeftAligned(
               GuiGraphics p_282318_,
               int p_283665_,
               int p_283416_,
               int p_281919_,
               int p_281686_) {
            int i = p_283416_;

            for (TextAndWidth multilinelabel$textandwidth : this.getSplitMessage()) {
               p_282318_.drawString(font, multilinelabel$textandwidth.text(), p_283665_, i, p_281686_, dropShadows);
               i += p_281919_;
            }

         }

         public int renderLeftAlignedNoShadow(GuiGraphics p_281782_, int p_282841_, int p_283554_, int p_282768_, int p_283499_) {
            int i = p_283554_;

            for(TextAndWidth multilinelabel$textandwidth : this.getSplitMessage()) {
               p_281782_.drawString(font, multilinelabel$textandwidth.text(), p_282841_, i, p_283499_, false);
               i += p_282768_;
            }

            return i;
         }

         private List<TextAndWidth> getSplitMessage() {
            Language language = Language.getInstance();
            if (this.cachedTextAndWidth != null && language == this.splitWithLanguage) {
               return this.cachedTextAndWidth;
            } else {
               this.splitWithLanguage = language;
               List<FormattedCharSequence> list = new ArrayList();

               for (Component component : components) {
                  list.addAll(font.split(component, maxWidth));
               }

               this.cachedTextAndWidth = new ArrayList();

               for (FormattedCharSequence formattedcharsequence : list.subList(0, Math.min(list.size(), maxRows))) {
                  this.cachedTextAndWidth
                        .add(new TextAndWidth(formattedcharsequence, font.width(formattedcharsequence)));
               }

               return this.cachedTextAndWidth;
            }
         }

         public int getLineCount() {
            return this.getSplitMessage().size();
         }

         public int getWidth() {
            return Math.min(maxWidth, this.getSplitMessage().stream().mapToInt(TextAndWidth::width).max().orElse(0));
         }
      };
   }

   public static void drawCenteredString(
         GuiGraphics graphics,
         Font font,
         FormattedCharSequence text,
         int x,
         int y,
         int color,
         boolean dropShadows) {
      graphics.drawString(font, text, x - font.width(text) / 2, y, color, dropShadows);
   }
}
