package com.uncreated.civilized.ui.menu.building;

import static com.uncreated.civilized.CivilizedMod.CIVILIZED_MOD_ID;

import java.util.List;

import com.uncreated.civilized.ui.style.Colors;
import com.uncreated.civilized.ui.tabs.AMenuScreenWithTabs;
import com.uncreated.civilized.ui.tabs.ATab;
import com.uncreated.civilized.ui.tabs.BlankTab;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class BuildingScreen extends AMenuScreenWithTabs<BuildingMenu> {
   private static final ResourceLocation CONTAINER_LOCATION =
         ResourceLocation.fromNamespaceAndPath(CIVILIZED_MOD_ID, "textures/gui/building_menu.png");

   private Button tab1;
   private Button tab2;
   private Button tab3;

   public BuildingScreen(BuildingMenu menu, Inventory playerInventory, Component title) {
      super(menu, playerInventory, title);
      this.imageWidth = 256;
      this.imageHeight = 256;
   }

   private final int tabWidth = 140;
   private final int tabHeight = 140;

   @Override
   protected List<ATab> createTabs() {
      return List.of(
            new InformationTab(
                  0,
                  getContentLeftPos(),
                  getContentTopPos(),
                  tabWidth,
                  tabHeight,
                  this.font,
                  menu.getBuilding(),
                  menu.getSettlement()),
            new OccupantsTab(
                  1,
                  getContentLeftPos(),
                  getContentTopPos(),
                  tabWidth,
                  tabHeight,
                  this.font,
                  menu.getBuilding(),
                  menu.getSettlement()),
            new BlankTab(
                  2,
                  getContentLeftPos(),
                  getContentTopPos(),
                  tabWidth,
                  tabHeight,
                  this.font,
                  "This is Tab #3 :)"));
   }

   protected int centerAlignedX(Component component) {
      return (this.imageWidth - this.font.width(component)) / 2;
   }

   protected void init() {
      super.init();
      this.titleLabelX = centerAlignedX(this.title);
      this.titleLabelY = 30;
      // net.minecraft.client.gui.components.

      tab1 =
            addRenderableWidget(
                  Button.builder(Component.literal("I"), this::onClickTab1)
                        .pos(leftPos + 20, topPos + 20)
                        .size(18, 18)
                        .tooltip(Tooltip.create(Component.literal("Information")))
                        .build());

      tab2 =
            addRenderableWidget(
                  Button.builder(Component.literal("R"), this::onClickTab2)
                        .pos(leftPos + 20, topPos + 40)
                        .size(18, 18)
                        .tooltip(Tooltip.create(Component.literal("Manage Residents")))
                        .build());

      tab3 =
            addRenderableWidget(
                  Button.builder(Component.literal("O"), this::onClickTab3)
                        .pos(leftPos + 20, topPos + 60)
                        .size(18, 18)
                        .tooltip(Tooltip.create(Component.literal("Building Settings")))
                        .build());

      changeToDefaultTabIfNotSet();

      // autoAssignOccupants =
      // addRenderableWidget(
      // Checkbox.builder(Component.literal("Auto assign residents"), this.font)
      // .pos(leftPos + MARGIN_X, topPos + 160)
      // .selected(true)
      // .build());
   }

   private void onClickTab1(Button button) {
      changeTab(0);
   }

   private void onClickTab2(Button button) {
      changeTab(1);
   }

   private void onClickTab3(Button button) {
      changeTab(2);
   }

   public void render(GuiGraphics graphics, int mouseX, int mouseY, float idkSomeNumber) {
      this.renderBackground(graphics, mouseX, mouseY, idkSomeNumber);
      super.render(graphics, mouseX, mouseY, idkSomeNumber);
      renderCurrentTabContents(graphics, mouseX, mouseY, idkSomeNumber);
      this.renderTooltip(graphics, mouseX, mouseY);
   }

   @Override
   protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
      graphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, Colors.MENU_TEXT_DARK, false);
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

   @Override
   public int getContentLeftPos() {
      return leftPos + 60;
   }

   @Override
   public int getContentTopPos() {
      return topPos + 50;
   }

   @Override
   public int getContentWidth() {
      return 140;
   }

   @Override
   public int getContentHeight() {
      return 150;
   }
}
