package com.uncreated.civilized.ui.tabs;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import com.uncreated.civilized.ui.components.IRefreshableUI;

import lombok.Getter;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;

public abstract class AMenuScreenWithTabs<T extends AbstractContainerMenu> extends AbstractContainerScreen<T>
      implements ITabContainer, IRefreshableUI {
   private List<ATab> tabs;
   @Getter
   @Nullable
   private ATab currentTab;

   public AMenuScreenWithTabs(T menu, Inventory playerInventory, Component title) {
      super(menu, playerInventory, title);
   }

   protected abstract List<ATab> createTabs();

   @Override
   protected void init() {
      super.init();
      tabs = createTabs();
      if (tabs.isEmpty()) {
         throw new IllegalArgumentException("Tabs list cannot be empty.");
      }
      // WARNING: this line seems to cause misaligned button clicking for some reason

   }

   public ATab changeTab(int newTabIndex) {
      if (currentTab != null) {
         removeWidget(currentTab);
      }

      currentTab = tabs.get(newTabIndex);
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
      currentTab.render(graphics, mouseX, mouseY, partialTick);
   }

   @Override
   public void render(GuiGraphics graphics, int mouseX, int mouseY, float idkSomeNumber) {
      // this.renderBackground(graphics, mouseX, mouseY, idkSomeNumber);
      super.render(graphics, mouseX, mouseY, idkSomeNumber);
      renderCurrentTabContents(graphics, mouseX, mouseY, idkSomeNumber);
      this.renderTooltip(graphics, mouseX, mouseY);
   }

   public void refresh() {
      if (currentTab != null)
         currentTab.refresh();
   }
}
