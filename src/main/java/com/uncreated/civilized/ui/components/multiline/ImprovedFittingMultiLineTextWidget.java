package com.uncreated.civilized.ui.components.multiline;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractTextAreaWidget;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ImprovedFittingMultiLineTextWidget extends AbstractTextAreaWidget {
   private final Font font;
   private final ImprovedMultiLineTextWidget multilineWidget;

   public ImprovedFittingMultiLineTextWidget(int x, int y, int width, int height, Component message, Font font) {
      super(x, y, width, height, message);
      this.font = font;
      this.multilineWidget =
            (new ImprovedMultiLineTextWidget(message, font)).setMaxWidth(this.getWidth() - this.totalInnerPadding()).dropShadows(false);
   }

   public ImprovedFittingMultiLineTextWidget setColor(int color) {
      this.multilineWidget.setColor(color);
      return this;
   }

   public void setWidth(int p_289765_) {
      super.setWidth(p_289765_);
      this.multilineWidget.setMaxWidth(this.getWidth() - this.totalInnerPadding());
   }

   public ImprovedFittingMultiLineTextWidget dropShadows(boolean enabled) {
      this.multilineWidget.dropShadows(enabled);
      return this;
   }

   protected int getInnerHeight() {
      return this.multilineWidget.getHeight();
   }

   protected double scrollRate() {
      return (double) 9.0F;
   }

   protected void renderBackground(GuiGraphics p_289758_) {
      if (this.scrollbarVisible()) {
         super.renderBackground(p_289758_);
      } else if (this.isFocused()) {
         this.renderBorder(
               p_289758_,
               this.getX() - this.innerPadding(),
               this.getY() - this.innerPadding(),
               this.getWidth() + this.totalInnerPadding(),
               this.getHeight() + this.totalInnerPadding());
      }

   }

   public void renderWidget(GuiGraphics p_289802_, int p_289778_, int p_289798_, float p_289804_) {
      if (this.visible) {
         if (!this.scrollbarVisible()) {
            this.renderBackground(p_289802_);
            p_289802_.pose().pushPose();
            p_289802_.pose().translate((float) this.getX(), (float) this.getY(), 0.0F);
            this.multilineWidget.render(p_289802_, p_289778_, p_289798_, p_289804_);
            p_289802_.pose().popPose();
         } else {
            super.renderWidget(p_289802_, p_289778_, p_289798_, p_289804_);
         }
      }

   }

   public boolean showingScrollBar() {
      return super.scrollbarVisible();
   }

   protected void renderContents(GuiGraphics p_289766_, int p_289790_, int p_289786_, float p_289767_) {
      p_289766_.pose().pushPose();
      p_289766_.pose().translate((float) this.getInnerLeft(), (float) this.getInnerTop(), 0.0F);
      this.multilineWidget.render(p_289766_, p_289790_, p_289786_, p_289767_);
      p_289766_.pose().popPose();
   }

   protected void updateWidgetNarration(NarrationElementOutput p_289784_) {
      p_289784_.add(NarratedElementType.TITLE, this.getMessage());
   }
}
