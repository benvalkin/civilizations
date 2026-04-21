package com.uncreated.civilized.ui.components;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

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
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class ScrollListView<Model, ElementWidget extends AbstractWidget> extends AbstractContainerWidget {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final ResourceLocation SCROLLER_SPRITE = ResourceLocation.withDefaultNamespace("widget/scroller");
   private static final ResourceLocation SCROLLER_BACKDROP_SPRITE =
         ResourceLocation.withDefaultNamespace("widget/scroller_background");
   private final ImageButton scrollBar;
   @org.jetbrains.annotations.NotNull
   private final IListViewBuilder<Model, ElementWidget> listViewBuilder;
   private boolean isDragging = false;

   private final List<Model> modelData;

   private int contentLeftPos;
   private int contentTopPos;
   private final int elementSpacing;

   private int scrollIndex = 0;
   private int maxElementsInView;

   private int minScrollbarY;
   private int maxScrollbarY;
   private int scrollbarScrollableHeight;

   private static final int SCROLLBAR_WIDTH = 6;

   public ScrollListView(
         int x,
         int y,
         int width,
         int height,
         int elementSpacing,
         IListViewBuilder<Model, ElementWidget> listViewBuilder) {
      super(x, y, width, height, Component.literal("Scroll View"));
      contentLeftPos = x;
      contentTopPos = y;
      this.elementSpacing = elementSpacing;
      modelData = listViewBuilder.provideModelData();

      scrollBar =
            new ImageButton(
                  contentLeftPos + width - SCROLLBAR_WIDTH,
                  contentTopPos,
                  SCROLLBAR_WIDTH,
                  20,
                  new WidgetSprites(SCROLLER_SPRITE, SCROLLER_SPRITE, SCROLLER_SPRITE, SCROLLER_SPRITE),
                  this::onPress);
      this.listViewBuilder = listViewBuilder;

      computeScrollbarSizeAndMaxElements(height, elementSpacing);

      visibleElementWidgets = new ArrayList<>();

      rebuildVisibleElementWidgets();
   }

   private void computeScrollbarSizeAndMaxElements(int height, int elementSpacing) {
      for (int i = 0; i < modelData.size(); i++) {
         int currentAggregateElementHeight = (i + 1) * elementSpacing;
         if (currentAggregateElementHeight <= height)
            maxElementsInView += 1;
         else
            break;
      }

      minScrollbarY = contentTopPos;
      float scrollBarSizeFactor =
            1f / (modelData.size() - maxElementsInView/* + 1 // add 1 for complete even scroll bar size */);
      int newScrollBarHeight = Math.clamp(Math.round(scrollBarSizeFactor * height), 8, height / 2);
      scrollBar.setHeight(newScrollBarHeight);
      maxScrollbarY = contentTopPos + contentHeight() - scrollBar.getHeight();
      scrollbarScrollableHeight = maxScrollbarY - minScrollbarY;
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

      for (ElementWidget elementWidget : visibleElementWidgets) {
         elementWidget.render(guiGraphics, mouseX, mouseY, partialTick);
      }

      renderScrollbar(guiGraphics, mouseX, mouseY, partialTick);
   }

   private List<ElementWidget> visibleElementWidgets;

   private void rebuildVisibleElementWidgets() {

      visibleElementWidgets.clear();

      int i = 0;
      while (i < maxElementsInView) {
         int displayIndex = i + scrollIndex;
         if (displayIndex >= modelData.size())
            break;

         Model currentModel = modelData.get(displayIndex);
         int elementY = getY() + i * elementSpacing;
         int elemntWidth = width - 10;
         ElementWidget elementWidget =
               listViewBuilder.buildElementWidgetFromModel(
                     displayIndex,
                     currentModel,
                     getX(),
                     elementY,
                     elemntWidth,
                     elementSpacing,
                     elementSpacing);

         visibleElementWidgets.add(elementWidget);

         i++;
      }
   }

   @Nullable
   private Integer mouseYStartDragging;

   private void renderScrollbar(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {

      if (modelData.size() <= maxElementsInView)
         return;

      if (isDragging) {

         if (mouseYStartDragging == null) {
            mouseYStartDragging = scrollBar.getY();
         }

         // int mouseYDiff = mouseY - previousMouseY;
         int mouseYDiffSinceStartDragging = mouseY - mouseYStartDragging;

         int pendingNewScrollbarY = scrollBar.getY() + mouseYDiffSinceStartDragging;
         float pendingNewScrollPercent = (pendingNewScrollbarY - minScrollbarY) / (float) scrollbarScrollableHeight;
         int pendingNewScrollIndex = Math.round((modelData.size() - maxElementsInView) * pendingNewScrollPercent);
         pendingNewScrollIndex = Math.clamp(pendingNewScrollIndex, 0, modelData.size() - maxElementsInView);

         if (pendingNewScrollIndex != scrollIndex) {
            mouseYStartDragging = null;
            scrollIndex = pendingNewScrollIndex;
            float realizedScrollBarPercentage = scrollIndex / (float) (modelData.size() - maxElementsInView);
            int realizedScrollBarHeight = Math.round(scrollbarScrollableHeight * realizedScrollBarPercentage);
            int newScrollbarY = getY() + realizedScrollBarHeight;
            scrollBar.setY(newScrollbarY);
            rebuildVisibleElementWidgets();
         }
      }
      guiGraphics.blitSprite(
            RenderType::guiTextured,
            SCROLLER_BACKDROP_SPRITE,
            this.getX() + this.width - SCROLLBAR_WIDTH,
            this.getY(),
            SCROLLBAR_WIDTH,
            this.height);
      scrollBar.render(guiGraphics, mouseX, mouseY, partialTick);
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
      List<GuiEventListener> children = new ArrayList<>(visibleElementWidgets);
      children.add(scrollBar);
      return children;
   }

   @Override
   public boolean mouseScrolled(double p_388530_, double p_387300_, double p_388604_, double p_386550_) {
      if (!this.visible) {
         return false;
      } else {
         this.setScrollAmount(this.scrollAmount() - p_386550_ * this.scrollRate());
         LOGGER.info("Scroll requiredAmountToTake: " + scrollAmount());
         return true;
      }
   }
}
