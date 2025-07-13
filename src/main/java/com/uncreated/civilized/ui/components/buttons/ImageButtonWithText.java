package com.uncreated.civilized.ui.components.buttons;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;

public class ImageButtonWithText extends Button {

    private final WidgetSprites widgetSprites;

    public ImageButtonWithText(Builder builder, WidgetSprites widgetSprites) {
      super(builder);
        this.widgetSprites = widgetSprites;
    }

   @Override
   protected void renderWidget(GuiGraphics p_281670_, int p_282682_, int p_281714_, float p_282542_) {
      Minecraft minecraft = Minecraft.getInstance();
      p_281670_.blitSprite(RenderType::guiTextured, widgetSprites.get(this.active, this.isHoveredOrFocused()), this.getX(), this.getY(), this.getWidth(), this.getHeight(), ARGB.white(this.alpha));
      int i = this.getFGColor();
      this.renderString(p_281670_, minecraft.font, i | Mth.ceil(this.alpha * 255.0F) << 24);
   }
}
