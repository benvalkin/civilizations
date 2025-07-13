package com.uncreated.civilized.ui.components.buttons;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.util.Mth;

public class TransparentButton extends Button {

   public TransparentButton(Builder builder) {
      super(builder);
   }

   @Override
   protected void renderWidget(GuiGraphics p_281670_, int p_282682_, int p_281714_, float p_282542_) {
      Minecraft minecraft = Minecraft.getInstance();
      // omit blit sprites
      int i = this.getFGColor();
      this.renderString(p_281670_, minecraft.font, i | Mth.ceil(this.alpha * 255.0F) << 24);
   }
}
