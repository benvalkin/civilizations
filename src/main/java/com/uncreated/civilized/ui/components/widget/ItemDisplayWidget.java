package com.uncreated.civilized.ui.components.widget;

import static net.minecraft.client.gui.screens.Screen.getTooltipFromItem;

import java.util.List;

import javax.annotation.Nullable;

import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class ItemDisplayWidget extends AbstractWidget {

   @Getter
   private final Slot slot;
   private boolean isHovering;
   private final Font font;

   public ItemDisplayWidget(int x, int y, ItemStack itemStack) {
      super(x, y, 16, 16, Component.empty());
      this.slot = new Slot(new SimpleContainer(), 0, x, y);
      this.slot.set(itemStack);
      this.isHovering = false;
      this.font = Minecraft.getInstance().font;
   }

   @Override
   protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
      renderItem(graphics, mouseX, mouseY, partialTicks);
   }

   @Override
   protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

   }

   protected void renderItem(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {

      this.isHovering = isHovering(mouseX, mouseY);

      if (slot.isActive()) {
         this.renderSlot(graphics, slot);
      }

      renderHoveredItemTooltip(graphics, mouseX, mouseY);
   }

   private void renderSlot(GuiGraphics guiGraphics, Slot slot) {
      int x = slot.x;
      int y = slot.y;
      ItemStack itemstack = slot.getItem();
      String s = null;

      guiGraphics.pose().pushPose();
      guiGraphics.pose().translate(0.0F, 0.0F, 100.0F);
      boolean missingItemTexture = false;
      if (itemstack.isEmpty() && slot.isActive()) {
         ResourceLocation resourcelocation = slot.getNoItemIcon();
         if (resourcelocation != null) {
            guiGraphics.blitSprite(RenderType::guiTextured, resourcelocation, x, y, 16, 16);
            missingItemTexture = true;
         }
      }

      if (!missingItemTexture) {
         // guiGraphics.fill(x, y, x + 16, y + 16, -2130706433);
         this.renderSlotContents(guiGraphics, itemstack, slot, s);
      }

      guiGraphics.pose().popPose();
   }

   private void renderSlotContents(
         GuiGraphics guiGraphics,
         ItemStack itemstack,
         Slot slot,
         @Nullable String countString) {
      int x = slot.x;
      int y = slot.y;
      int seed = slot.x + slot.y * this.width;
      if (slot.isFake()) {
         guiGraphics.renderFakeItem(itemstack, x, y, seed);
      } else {
         guiGraphics.renderItem(itemstack, x, y, seed);
      }

      guiGraphics.renderItemDecorations(this.font, itemstack, x, y, countString);
   }

   private boolean isHovering(double mouseX, double mouseY) {
      return isHovering(slot.x, slot.y, 16, 16, mouseX, mouseY);
   }

   private boolean isHovering(int x, int y, int width, int height, double mouseX, double mouseY) {
      return mouseX >= (double) (x - 1) && mouseX < (double) (x + width + 1) && mouseY >= (double) (y - 1)
            && mouseY < (double) (y + height + 1);
   }

   private void renderHoveredItemTooltip(GuiGraphics guiGraphics, int x, int y) {
      if (this.isHovering && this.slot.hasItem()) {
         ItemStack itemstack = this.slot.getItem();
         guiGraphics.renderTooltip(
               this.font,
               this.getTooltipFromContainerItem(itemstack),
               itemstack.getTooltipImage(),
               itemstack,
               x,
               y,
               itemstack.get(DataComponents.TOOLTIP_STYLE));
      }
   }

   protected List<Component> getTooltipFromContainerItem(ItemStack stack) {
      return getTooltipFromItem(Minecraft.getInstance(), stack);
   }
}
