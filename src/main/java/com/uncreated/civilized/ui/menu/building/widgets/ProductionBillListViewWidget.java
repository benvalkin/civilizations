package com.uncreated.civilized.ui.menu.building.widgets;

import java.util.List;

import com.uncreated.civilized.core.StoreOperation;
import com.uncreated.civilized.core.building.Building;
import com.uncreated.civilized.core.building.ClientBuildingStore;
import com.uncreated.civilized.core.building.crafting.bills.ProductionBill;
import com.uncreated.civilized.core.building.crafting.bills.strategy.ProductionStrategyType;
import com.uncreated.civilized.networking.packets.RequestEditRecipeScreenScreen;
import com.uncreated.civilized.ui.components.widget.ItemDisplayWidget;
import com.uncreated.civilized.ui.style.Colors;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractContainerWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.network.PacketDistributor;

public class ProductionBillListViewWidget extends AbstractContainerWidget {

   protected final Font font;
   private final int productionBillIndex;
   protected final Building building;
   protected final ProductionBill productionBill;
   private final Button editButton;
   private final ItemDisplayWidget itemDisplay;
   private final Checkbox enabledButton;

   public ProductionBillListViewWidget(
         int x,
         int y,
         int width,
         int height,
         Font font,
         ProductionBill productionBill,
         int productionBillIndex,
         Building building) {
      super(x, y, width, height, Component.literal("ManageOccupantWidget"));
      this.font = font;
      this.productionBillIndex = productionBillIndex;
      this.productionBill = productionBill;
      this.building = building;

      itemDisplay = new ItemDisplayWidget(x + 4, y, productionBill.getDisplayItem());
      enabledButton =
            Checkbox.builder(Component.empty(), font)
                  .onValueChange(this::onProductionBillToggled)
                  .tooltip(Tooltip.create(Component.translatable("production_bill.description.enabled")))
                  .pos(x + width - 70, y)
                  .selected(productionBill.isEnabled())
                  .build();
      editButton =
            Button.builder(Component.translatable("gui.misc.button.edit"), this::onPress)
                  .pos(x + width - 50, y)
                  .size(50, 17)
                  .build();

   }

   private void onProductionBillToggled(Checkbox checkbox, boolean enabled) {
      productionBill.setEnabled(enabled);
      ClientBuildingStore.INSTANCE.replicateChange(building, StoreOperation.UPDATE);
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

      guiGraphics.drawString(
            font,
            ProductionStrategyType.getBillStrategyDescription(productionBill),
            getX() + 28,
            getY() + 6,
            Colors.MENU_TEXT_DARK,
            false);

      editButton.render(guiGraphics, mouseX, mouseY, partialTick);
      enabledButton.render(guiGraphics, mouseX, mouseY, partialTick);
      itemDisplay.render(guiGraphics, mouseX, mouseY, partialTick);
   }

   @Override
   protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

   }

   @Override
   public List<? extends GuiEventListener> children() {
      return List.of(editButton, enabledButton, itemDisplay);
   }

   private void onPress(Button b) {
      PacketDistributor
            .sendToServer(new RequestEditRecipeScreenScreen(building.getBuildingId(), 9, productionBillIndex, false));
   }
}
