package com.uncreated.civilized.ui.components.buttons;

import com.uncreated.civilized.ui.StringRenderHelper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;

public class ImageButtonWithText extends Button {

   private final WidgetSprites widgetSprites;
   private final int labelPadding = 4;

   public ImageButtonWithText(Builder builder, WidgetSprites widgetSprites) {
      super(builder);
      this.widgetSprites = widgetSprites;
      updateHeight(); // update height at the beginning before this button is rendered for the first time - this helps
                      // when parent screens/widget need to know how tall this button will be when building list views
   }

   @Override
   public void renderString(GuiGraphics guiGraphics, Font font, int color) {

      // TODO: add wrapText flag - this current setup ignores height set by the builder

      StringRenderHelper.drawCenterAlignedWordWrap(
            guiGraphics,
            font,
            getMessage(),
            getX() + width / 2,
            getY() + labelPadding,
            width - labelPadding * 2,
            color,
            true);

      updateHeight();
   }

   private void updateHeight() {
      int textHeight =
            StringRenderHelper
                  .getHeightOfWrappedText(Minecraft.getInstance().font, getMessage(), width - labelPadding * 2);
      this.height = textHeight + labelPadding * 2;
   }

   @Override
   protected void renderWidget(GuiGraphics p_281670_, int p_282682_, int p_281714_, float p_282542_) {
      Minecraft minecraft = Minecraft.getInstance();
      p_281670_.blitSprite(
            RenderType::guiTextured,
            widgetSprites.get(this.active, this.isHoveredOrFocused()),
            this.getX(),
            this.getY(),
            this.getWidth(),
            this.getHeight(),
            ARGB.white(this.alpha));
      int i = this.getFGColor();
      this.renderString(p_281670_, minecraft.font, i | Mth.ceil(this.alpha * 255.0F) << 24);
   }
}
