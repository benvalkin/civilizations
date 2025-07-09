package com.uncreated.civilized.ui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class TestContainerScreen extends AbstractContainerScreen<TestContainerMenu> {
   private static final ResourceLocation CONTAINER_LOCATION =
         ResourceLocation.withDefaultNamespace("textures/gui/container/dispenser.png");
   private static final int OFFSET_X = 10;
   private Button buttonConfirm;

   public TestContainerScreen(TestContainerMenu menu, Inventory playerInventory, Component title) {
      super(menu, playerInventory, title);
   }

   private void onClickConfirm(Button button) {
   }

   protected void init() {
      super.init();
      this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;

      buttonConfirm =
            Button.builder(Component.literal("Done"), this::onClickConfirm)
                  .pos(leftPos + OFFSET_X, topPos + 10)
                  .size(30, 18)
                  .tooltip(Tooltip.create(Component.literal("Hello Cuh")))
                  .build();

      addRenderableWidget(buttonConfirm);

   }

   public void render(GuiGraphics p_283282_, int p_282467_, int p_282129_, float p_281965_) {
      this.renderBackground(p_283282_, p_282467_, p_282129_, p_281965_);
      super.render(p_283282_, p_282467_, p_282129_, p_281965_);
      this.renderTooltip(p_283282_, p_282467_, p_282129_);
   }

   protected void renderBg(GuiGraphics p_283137_, float p_282476_, int p_281600_, int p_283194_) {
      int i = (this.width - this.imageWidth) / 2;
      int j = (this.height - this.imageHeight) / 2;
      p_283137_.blit(
            RenderType::guiTextured,
            CONTAINER_LOCATION,
            i,
            j,
            0.0F,
            0.0F,
            this.imageWidth,
            this.imageHeight,
            256,
            256);
   }
}
