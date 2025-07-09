package com.uncreated.civilized.ui.components;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractContainerWidget;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class ScrollListView extends AbstractContainerWidget {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final ResourceLocation SCROLLER_SPRITE =
         ResourceLocation.withDefaultNamespace("container/villager/scroller");
   private final ImageButton scrollBar;
   private boolean isDragging = false;
   int scrollOff;

   private List<AbstractWidget> children;
   private final List<AbstractWidget> elements;

   private int contentLeftPos;
   private int contentTopPos;

   int scrollIndex = 0;
   private final int maxElementsInView = 5;

   int previousMouseY = 0;

   private int numberOfElementsInView;

   private final int minScrollbarY;
   private final int maxScrollbarY;
   private final int scrollbarScrollableHeight;

   public ScrollListView(int x, int y, int width, int height, IViewBuilder<List<AbstractWidget>> createElements) {
      super(x, y, width, height, Component.literal("Scroll View"));
      contentLeftPos = x;
      contentTopPos = y;
      elements = createElements.buildView(x, y, width, height);

      scrollBar =
            new ImageButton(
                  contentLeftPos + width - 8,
                  contentTopPos,
                  8,
                  20,
                  new WidgetSprites(SCROLLER_SPRITE, SCROLLER_SPRITE, SCROLLER_SPRITE, SCROLLER_SPRITE),
                  this::onPress);

      minScrollbarY = contentTopPos;
      maxScrollbarY = contentTopPos + contentHeight() - scrollBar.getHeight();
      scrollbarScrollableHeight = maxScrollbarY - minScrollbarY;

      numberOfElementsInView = Math.min(maxElementsInView, elements.size());
   }

   @Override
   protected int contentHeight() {
      return height;
   }

   @Override
   protected double scrollRate() {
      return 1;
   }

   @Override
   protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {

      for (int i = 0; i < numberOfElementsInView; i++) {
         int displayIndex = i + scrollIndex;
         if (displayIndex >= elements.size())
            continue;

         AbstractWidget child = elements.get(displayIndex);
         // child.setY(contentTopPos + i * elementSpacing);

         // guiGraphics.pose().pushPose();
         // guiGraphics.pose().translate(getX(), getY(), 0);
         child.render(guiGraphics, mouseX, mouseY, partialTick);
         // guiGraphics.pose().popPose();
      }

      renderScrollbar(guiGraphics, mouseX, mouseY, partialTick);
   }

   private void renderScrollbar(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
      // guiGraphics.pose().pushPose();
      // guiGraphics.pose().translate(getX(), getY(), 0);
      if (isDragging() && elements.size() > maxElementsInView) {
         int mouseYDiff = mouseY - previousMouseY;
         int newScrollbarY = scrollBar.getY() + mouseYDiff;

         if (mouseYDiff != 0 && newScrollbarY >= minScrollbarY && newScrollbarY <= maxScrollbarY) {
            scrollBar.setY(newScrollbarY);
         }

         float scrolledPercent = (scrollBar.getY() - minScrollbarY) / (float) scrollbarScrollableHeight;
         scrollIndex = Math.round((elements.size() - maxElementsInView) * scrolledPercent);
      }
      scrollBar.render(guiGraphics, mouseX, mouseY, partialTick);
      previousMouseY = mouseY;
      // guiGraphics.pose().popPose();
   }

   private void onPress(Button b) {
      isDragging = true;
   }

   @Override
   public boolean mouseReleased(double p_313886_, double p_313935_, int p_313751_) {
      isDragging = false;
      return super.mouseReleased(p_313886_, p_313935_, p_313751_);
   }

   @Override
   protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

   }

   @Override
   public List<? extends GuiEventListener> children() {
      children = new ArrayList<>();
      children.addAll(elements);
      children.add(scrollBar);
      return children;
   }

   @Override
   public boolean mouseScrolled(double p_388530_, double p_387300_, double p_388604_, double p_386550_) {
      if (!this.visible) {
         return false;
      } else {
         this.setScrollAmount(this.scrollAmount() - p_386550_ * this.scrollRate());
         LOGGER.info("Scroll amount: " + scrollAmount());
         return true;
      }
   }
}
