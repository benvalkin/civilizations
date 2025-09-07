package com.uncreated.civilized.ui.menu.building.item.management;

import com.uncreated.civilized.core.building.Building;
import com.uncreated.civilized.core.settlement.Settlement;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public abstract class ItemManagementScreen<T extends ItemManagementMenu> extends AbstractContainerScreen<T> {

   protected final Building building;
   protected final Settlement settlement;

   protected int imageHeight;

   public ItemManagementScreen(T menu, Inventory playerInventory, Component title) {
      super(menu, playerInventory, title);
      this.building = menu.getBuilding();
      this.settlement = menu.getSettlement();
      this.imageWidth = 340;
      this.imageHeight = 200;
      this.inventoryLabelX = 90;
      this.inventoryLabelY = this.imageHeight - 125;
      this.titleLabelY = 10;
      this.titleLabelX = 30;
   }

   @Override
   protected void init() {
      super.init();
   }

   private void onPressDone(Button button) {
      Minecraft.getInstance().player.closeContainer(); // for some reason, menu.stopOpen doesn't sync to server
   }

   public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
      super.render(graphics, mouseX, mouseY, partialTicks);
      this.renderTooltip(graphics, mouseX, mouseY);
   }
}
