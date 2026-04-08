package com.uncreated.civilized.ui.menu.building.item.management;

import com.uncreated.civilized.core.building.Building;
import com.uncreated.civilized.core.settlement.Settlement;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public abstract class ItemManagementScreen<T extends ItemManagementMenu> extends AbstractContainerScreen<T> {

   private final ResourceLocation menuTexture;
   protected final Building building;
   protected final Settlement settlement;
   protected final int contentXMargin;
   protected final int contentYMargin;
   protected int contentXStart;
   protected int contentYStart;
   protected int contentXEnd;
   protected int contentYEnd;

   protected int imageHeight; // it seems like shadowing the base AbstractContainerScreen's imageHeight (but not
                              // imageWidth) makes the scaling work nicely...

   private Button done;

   public ItemManagementScreen(ResourceLocation menuTexture, T menu, Inventory playerInventory, Component title) {
      super(menu, playerInventory, title);
      this.menuTexture = menuTexture;
      this.building = menu.getBuilding();
      this.settlement = menu.getSettlement();
      this.imageWidth = 340;
      this.imageHeight = 200;
      this.contentXMargin = 25;
      this.contentYMargin = 20;
      this.inventoryLabelX = 90;
      this.inventoryLabelY = this.imageHeight - 125;
      this.titleLabelY = 2;
      this.titleLabelX = 30;
   }

   @Override
   protected void init() {
      super.init();

      contentXStart = (width - imageWidth) / 2 + contentXMargin;
      contentYStart = (height - imageHeight) / 2 + contentYMargin;
      contentXEnd = width - (width - imageWidth) / 2 - contentXMargin;
      contentYEnd = height - (height - imageHeight) / 2 - contentYMargin;
      done =
            Button.builder(Component.translatable("gui.misc.button.done"), this::onPressDone)
                  .pos(contentXEnd - 60, contentYEnd - 18)
                  .size(60, 18)
                  .build();

      addRenderableWidget(done);
   }

   protected void onPressDone(Button button) {
      Minecraft.getInstance().player.closeContainer(); // for some reason, menu.stopOpen doesn't sync to server
   }

   public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
      super.render(graphics, mouseX, mouseY, partialTicks);
      this.renderTooltip(graphics, mouseX, mouseY);
   }

   protected void renderBg(GuiGraphics p_281616_, float p_282737_, int p_281678_, int p_281465_) {
      int i = (this.width - this.imageWidth) / 2;
      int j = (this.height - this.imageHeight) / 2;
      p_281616_
            .blit(RenderType::guiTextured, menuTexture, i, j, 0.0F, 0.0F, this.imageWidth, this.imageHeight, 384, 384);
   }
}
