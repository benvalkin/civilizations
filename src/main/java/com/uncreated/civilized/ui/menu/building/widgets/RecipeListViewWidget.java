package com.uncreated.civilized.ui.menu.building.widgets;

import java.util.List;

import com.uncreated.civilized.core.building.Building;
import com.uncreated.civilized.core.building.crafting.bills.ProductionBill;
import com.uncreated.civilized.ui.components.widget.ItemDisplayWidget;
import com.uncreated.civilized.ui.style.Colors;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractContainerWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

public class RecipeListViewWidget extends AbstractContainerWidget {

   protected final Font font;
   protected final Building building;
   protected final ProductionBill productionBill;
   private final Button button;
   private final ItemDisplayWidget itemDisplay;

   public RecipeListViewWidget(
         int x,
         int y,
         int width,
         int height,
         Font font,
         ProductionBill productionBill,
         Building building) {
      super(x, y, width, height, Component.literal("ManageOccupantWidget"));
      this.font = font;
      this.building = building;
      this.productionBill = productionBill;

      itemDisplay = new ItemDisplayWidget(x, y, productionBill.getGetDisplayItem());
      button =
            Button.builder(Component.translatable("gui.misc.button.edit"), this::onPress)
                  .pos(x + width - 50, y + 10)
                  .size(50, 12)
                  .build();
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

      // draw icon

      guiGraphics.drawString(
            font,
            productionBill.getProductionStrategy().getType().translationKey(),
            getX(),
            getY(),
            Colors.MENU_TEXT_DARK,
            false);
      guiGraphics.drawString(
            font,
            Component.translatable("menu.building.residence.production_bills.heading.empty"),
            getX(),
            getY(),
            Colors.MENU_TEXT_DARK,
            false);

      button.render(guiGraphics, mouseX, mouseY, partialTick);
      itemDisplay.render(guiGraphics, mouseX, mouseY, partialTick);
   }

   @Override
   protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

   }

   @Override
   public List<? extends GuiEventListener> children() {
      return List.of(button);
   }

   private void onPress(Button b) {

      // show edit recipe screen

      // ClientBuildingStore.INSTANCE.replicateChange(building, StoreOperation.UPDATE);
   }
}
