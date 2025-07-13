package com.uncreated.civilized.ui.menu.building;

import static com.uncreated.civilized.CivilizedMod.CIVILIZED_MOD_ID;

import java.util.List;

import com.uncreated.civilized.ui.style.Colors;
import com.uncreated.civilized.ui.tabs.AMenuScreenWithTabs;
import com.uncreated.civilized.ui.tabs.ATab;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public abstract class ABuildingScreen extends AMenuScreenWithTabs<BuildingMenu> {
   private static final ResourceLocation CONTAINER_LOCATION =
         ResourceLocation.fromNamespaceAndPath(CIVILIZED_MOD_ID, "textures/gui/building_menu.png");

   public ABuildingScreen(BuildingMenu menu, Inventory playerInventory, Component title) {
      super(menu, playerInventory, title);
      this.imageWidth = 400;
      this.imageHeight = 400;
   }

   private final int tabWidth = 200;
   private final int tabHeight = 170;

   @Override
   protected final List<ATab> createTabs() {
      return createTabs(getContentLeftPos(), getContentTopPos(), tabWidth, tabHeight);
   }

   protected abstract List<ATab> createTabs(int contentLeftPos, int contentTopPos, int tabWidth, int tabHeight);

   protected abstract List<Button.Builder> createTabButtons();

   protected void init() {
      super.init();
      this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;
      this.titleLabelY = 130;
      // net.minecraft.client.gui.components.

      createTabButtons().forEach(builder -> addRenderableWidget(builder.build()));

      changeToDefaultTabIfNotSet();

      // autoAssignOccupants =
      // addRenderableWidget(
      // Checkbox.builder(Component.literal("Auto assign residents"), this.font)
      // .pos(leftPos + MARGIN_X, topPos + 160)
      // .selected(true)
      // .build());
   }

   @Override
   protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
      graphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, Colors.MENU_TEXT_DARK, false);
   }

   @Override
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
            400,
            400);
   }

   @Override
   public int getContentLeftPos() {
      return leftPos + 100;
   } // BAD IMPLEMENTATION: we don't methods and an interface for this

   @Override
   public int getContentTopPos() {
      return topPos + 145;
   }

   @Override
   public int getContentWidth() {
      return tabWidth;
   }

   @Override
   public int getContentHeight() {
      return tabHeight;
   }
}
