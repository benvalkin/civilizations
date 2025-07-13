package com.uncreated.civilized.ui.components.buttons;

import static com.uncreated.civilized.CivilizedMod.CIVILIZED_MOD_ID;

import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.resources.ResourceLocation;

public class ModernButton extends ImageButtonWithText {

   protected static final WidgetSprites SPRITES =
         new WidgetSprites(
               ResourceLocation.fromNamespaceAndPath(CIVILIZED_MOD_ID, "widget/button_modern"),
               ResourceLocation.fromNamespaceAndPath(CIVILIZED_MOD_ID, "widget/button_modern_disabled"),
               ResourceLocation.fromNamespaceAndPath(CIVILIZED_MOD_ID, "widget/button_modern_highlighted"));

   public ModernButton(Builder builder) {
      super(builder, SPRITES);
   }
}
