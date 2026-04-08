package com.uncreated.civilized.ui.components.buttons;

import static com.uncreated.civilized.CivilizedMod.CIVILIZED_MOD_ID;

import java.util.function.Consumer;
import java.util.function.Supplier;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class TrashcanButton extends Button {

   protected static final WidgetSprites SPRITES =
         new WidgetSprites(
               ResourceLocation.fromNamespaceAndPath(CIVILIZED_MOD_ID, "widget/button_trashcan"),
               ResourceLocation.fromNamespaceAndPath(CIVILIZED_MOD_ID, "widget/button_trashcan_disabled"),
               ResourceLocation.fromNamespaceAndPath(CIVILIZED_MOD_ID, "widget/button_trashcan_highlighted"));

   protected static final WidgetSprites SPRITES_CONFIRM =
         new WidgetSprites(
               ResourceLocation.fromNamespaceAndPath(CIVILIZED_MOD_ID, "widget/button_trashcan_confirm"),
               ResourceLocation.fromNamespaceAndPath(CIVILIZED_MOD_ID, "widget/button_trashcan_disabled"),
               ResourceLocation.fromNamespaceAndPath(CIVILIZED_MOD_ID, "widget/button_trashcan_confirm_highlighted"));

   private final Consumer<TrashcanButton> onDeleteConfirmed;

   boolean pressedOnce;

   public TrashcanButton(int x, int y, OnPress onPress, Consumer<TrashcanButton> onDeleteConfirmed) {
      super(x, y, 17, 17, Component.translatable("gui.misc.button.delete"), onPress, Supplier::get);
      this.onDeleteConfirmed = onDeleteConfirmed;
      pressedOnce = false;
   }

   @Override
   public void onPress() {

      if (!pressedOnce)
         pressedOnce = true;
      else {
         pressedOnce = false;
         onDeleteConfirmed.accept(this);
         setFocused(false);
      }

      super.onPress();
   }

   @Override
   protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
      ResourceLocation resourcelocation;
      if (pressedOnce) {
         resourcelocation = SPRITES_CONFIRM.get(this.isActive(), this.isHoveredOrFocused());
      } else {
         resourcelocation = SPRITES.get(this.isActive(), this.isHoveredOrFocused());
      }
      guiGraphics
            .blitSprite(RenderType::guiTextured, resourcelocation, this.getX(), this.getY(), this.width, this.height);
   }
}
