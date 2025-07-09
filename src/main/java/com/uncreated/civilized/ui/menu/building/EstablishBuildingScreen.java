package com.uncreated.civilized.ui.menu.building;

import static com.uncreated.civilized.CivilizedMod.CIVILIZED_MOD_ID;

import java.util.List;

import com.uncreated.civilized.block.building.requirement.IBuildingRequirementResult;
import com.uncreated.civilized.core.building.BuildingType;
import com.uncreated.civilized.ui.components.multiline.ImprovedMultiLineTextWidget;
import com.uncreated.civilized.ui.menu.building.widgets.BuildingRequirementWidget;
import com.uncreated.civilized.ui.style.Colors;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

/**
 * Screen that shows when placing and upgrading buildings.
 */
public class EstablishBuildingScreen extends Screen {
   private static final ResourceLocation CONTAINER_LOCATION =
         ResourceLocation.fromNamespaceAndPath(CIVILIZED_MOD_ID, "textures/gui/building_menu.png");

   private final int imageWidth;
   private final int imageHeight;
   private final List<IBuildingRequirementResult> requirements;
   private int leftPos;
   private int topPos;
   private int contentWidth;
   private int contentHeight;
   private int contentMarginX = 60;
   private int contentMarginY = 25;
   private int titleX;
   private int titleY;

   private ImprovedMultiLineTextWidget titleText;
   private Button cancel;
   private Button confirm;

   public EstablishBuildingScreen(BuildingType buildingType, List<IBuildingRequirementResult> requirements) {
      super(Component.translatable("menu.building.management.create.heading", buildingType.translationDark()));
      this.requirements = requirements;
      isPauseScreen();
      imageWidth = 256;
      imageHeight = 256;
   }

   @Override
   public boolean isPauseScreen() {
      return false;
   }

   protected void init() {
      super.init();
      this.leftPos = (width - this.imageWidth) / 2 + contentMarginX;
      this.topPos = (height - this.imageHeight) / 2 + contentMarginY;
      this.contentWidth = imageWidth - contentMarginX * 2;
      this.contentHeight = imageHeight - 100;
      this.titleX = leftPos;
      this.titleY = topPos;

      final int marginXTitle = 10;
      titleText = new ImprovedMultiLineTextWidget(leftPos + marginXTitle, topPos + 10, getTitle(), font);
      titleText.setCentered(true);
      titleText.setMaxWidth(contentWidth - marginXTitle);
      titleText.setColor(Colors.MENU_TEXT_DARK);
      titleText.dropShadows(false);

      final int buttonMargin = 2;
      final int buttonHeight = 20;
      cancel =
            Button.builder(Component.translatable("gui.misc.button.cancel"), EstablishBuildingScreen::onPressCancel)
                  .pos(leftPos + buttonMargin, topPos + contentHeight - buttonMargin)
                  .size(contentWidth / 2 - buttonMargin * 2, buttonHeight)
                  .build();

      confirm =
            Button.builder(Component.translatable("gui.misc.button.confirm"), EstablishBuildingScreen::onPressConfirm)
                  .pos(leftPos + contentWidth / 2 + buttonMargin, topPos + contentHeight - buttonMargin)
                  .size(contentWidth / 2 - buttonMargin * 2, buttonHeight)
                  .build();
      boolean allSatisfied = requirements.stream().allMatch(IBuildingRequirementResult::isSatisfied);
      if (!allSatisfied) {
         confirm.active = false;
         confirm.setTooltip(
               Tooltip.create(
                     Component.translatable("menu.building.management.requirements.tooltip.not_satisfied_hint")
                           .withColor(Colors.VALIDATION_ERROR)));
      }

      addRenderableOnly(titleText);
      addRenderableWidget(cancel);
      addRenderableWidget(confirm);

      int index = 0;
      int elementSpacing = 14;
      for (IBuildingRequirementResult requirement : requirements) {

         if (requirement.hideIfSatisfied() && requirement.isSatisfied())
            continue;

         BuildingRequirementWidget roofBlocksRequirementWidget =
               new BuildingRequirementWidget(
                     leftPos,
                     topPos + 50 + index * elementSpacing,
                     contentWidth,
                     elementSpacing,
                     9,
                     font,
                     requirement);

         addRenderableWidget(roofBlocksRequirementWidget);
         index++;
      }
   }

   private static void onPressCancel(Button button) {
      Minecraft.getInstance().setScreen(null);
   }

   private static void onPressConfirm(Button button) {

   }

   public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
      super.render(graphics, mouseX, mouseY, partialTicks);
      // this.renderBackground(graphics, mouseX, mouseY, partialTicks);
      this.renderLabels(graphics, mouseX, mouseY, partialTicks);

   }

   protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
      graphics.drawString(
            this.font,
            Component.translatable("menu.building.management.requirements.heading").withStyle(ChatFormatting.UNDERLINE),
            leftPos,
            topPos + 35,
            Colors.MENU_TEXT_DARK,
            false);
   }

   public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
      int i = (this.width - this.imageWidth) / 2;
      int j = (this.height - this.imageHeight) / 2;
      graphics.blit(
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
