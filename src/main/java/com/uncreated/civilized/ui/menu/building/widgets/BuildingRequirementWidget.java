package com.uncreated.civilized.ui.menu.building.widgets;

import static com.uncreated.civilized.CivilizedMod.CIVILIZED_MOD_ID;

import java.util.List;

import com.uncreated.civilized.core.building.requirement.IBuildingRequirementResult;
import com.uncreated.civilized.ui.components.multiline.ImprovedMultiLineTextWidget;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractContainerWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class BuildingRequirementWidget extends AbstractContainerWidget {

   // note that blitSprite omits the full path
   private static final ResourceLocation CHECKMARK =
         ResourceLocation.fromNamespaceAndPath(CIVILIZED_MOD_ID, "icon/checkmark");
   private static final ResourceLocation X = ResourceLocation.fromNamespaceAndPath(CIVILIZED_MOD_ID, "icon/x");

   private final int iconSize;
   private final Font font;
   @org.jetbrains.annotations.NotNull
   private final IBuildingRequirementResult requirement;
   private final ImprovedMultiLineTextWidget description;

   public BuildingRequirementWidget(
         int x,
         int y,
         int width,
         int height,
         int iconSize,
         Font font,
         IBuildingRequirementResult requirement) {
      super(x, y, width, height, Component.literal("ManageOccupantWidget"));
      this.iconSize = iconSize;
      this.font = font;
      this.requirement = requirement;

      description = new ImprovedMultiLineTextWidget(x, y, requirement.getDescription(), this.font).dropShadows(false);
      description.setMaxWidth(width - iconSize - 5);

      if (requirement.getTooltipDescription() != null)
         this.setTooltip(Tooltip.create(requirement.getTooltipDescription()));
   }

   @Override
   protected int contentHeight() {
      return height;
   }

   @Override
   protected double scrollRate() {
      return 0;
   }

   @Override
   protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
      description.renderWidget(guiGraphics, mouseX, mouseY, partialTick);

      guiGraphics.blitSprite(
            RenderType::guiTexturedOverlay,
            requirement.isSatisfied() ? CHECKMARK : X,
            getX() + width - iconSize,
            getY(),
            iconSize,
            iconSize);
   }

   @Override
   protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

   }

   @Override
   public List<? extends GuiEventListener> children() {
      return List.of(description);
   }
}
