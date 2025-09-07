package com.uncreated.civilized.ui.tabs;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import com.uncreated.civilized.ui.components.IRefreshableUI;
import com.uncreated.civilized.ui.style.Colors;

import lombok.Getter;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;

public abstract class AMenuScreenWithTabs extends Screen implements IRefreshableUI {
   private List<ATab> tabs;
   @Getter
   @Nullable
   private ATab currentTab;

   protected int leftPos;
   protected int topPos;

   protected final int imageWidth;
   protected final int imageHeight;

   public AMenuScreenWithTabs(Component title, int imageWidth, int imageHeight) {
      super(title);
       this.imageWidth = imageWidth;
       this.imageHeight = imageHeight;
   }

   protected abstract List<ATab> createTabs();

   @Override
   protected void init() {
      super.init();
      this.leftPos = (this.width - this.imageWidth) / 2;
      this.topPos = (this.height - this.imageHeight) / 2;
      tabs = createTabs();
      if (tabs.isEmpty()) {
         throw new IllegalArgumentException("Tabs list cannot be empty.");
      }
      // WARNING: this line seems to cause misaligned button clicking for some reason
   }

   public ATab changeTab(int newTabIndex) {
      if (currentTab != null) {
         currentTab.onClose();
         removeWidget(currentTab);
      }

      currentTab = tabs.get(newTabIndex);
      currentTab.init();
      refresh();
      addWidget(currentTab);

      return currentTab;
   }

   public ATab changeToDefaultTabIfNotSet() {
      if (currentTab == null) {
         changeTab(0);
      }
      return currentTab;
   }

   public void renderCurrentTabContents(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
      if (currentTab != null)
         currentTab.render(graphics, mouseX, mouseY, partialTick);
   }

   @Override
   public void render(GuiGraphics graphics, int mouseX, int mouseY, float idkSomeNumber) {
      super.render(graphics, mouseX, mouseY, idkSomeNumber);
      renderCurrentTabContents(graphics, mouseX, mouseY, idkSomeNumber);
      graphics.hLine(RenderType.guiOverlay(), 0, width, height / 2, Colors.VALIDATION_ERROR);

      // this.renderTooltip(graphics, mouseX, mouseY);
   }

   @Override
   public void renderBackground(GuiGraphics p_295206_, int p_295457_, int p_294596_, float p_296351_) {
      this.renderTransparentBackground(p_295206_);
      this.renderBg(p_295206_, p_296351_, p_295457_, p_294596_);
   }

   protected void renderBg(GuiGraphics p_283137_, float p_282476_, int p_281600_, int p_283194_) {
   }

   public void refresh() {
      if (currentTab != null)
         currentTab.refresh();
   }
}
