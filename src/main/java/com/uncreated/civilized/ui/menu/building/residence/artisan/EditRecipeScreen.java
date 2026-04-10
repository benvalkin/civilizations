package com.uncreated.civilized.ui.menu.building.residence.artisan;

import com.uncreated.civilized.core.building.production.bills.strategy.ProductionStrategyType;
import com.uncreated.civilized.networking.packets.EditProductionBillUpdateState;
import com.uncreated.civilized.ui.components.widget.ItemQuantitySelectorWidget;
import com.uncreated.civilized.ui.menu.building.item.management.ItemManagementScreen;
import com.uncreated.civilized.ui.style.Colors;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerListener;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;

public abstract class EditRecipeScreen<TEditRecipeMenu extends EditRecipeMenu<?, ?>> extends ItemManagementScreen<TEditRecipeMenu>
      implements ContainerListener {

   private CycleButton<ProductionStrategyType> changeStrategyButton;
   private ItemQuantitySelectorWidget itemQuantitySelectorWidget;

   public EditRecipeScreen(ResourceLocation backgroundTexture, TEditRecipeMenu menu, Inventory playerInventory, Component title) {
      super(backgroundTexture, menu, playerInventory, title);

   }

   private Component apply(ProductionStrategyType productionStrategyType) {
      return productionStrategyType.getSimpleDescription();
   }

   private void onChangeStrategyType(
         CycleButton<ProductionStrategyType> b,
         ProductionStrategyType productionStrategyType) {
      itemQuantitySelectorWidget.visible = productionStrategyType.requiresAmount();
      if (itemQuantitySelectorWidget.visible)
         itemQuantitySelectorWidget
               .setItemAndQuantity(menu.getOutputSlot().getItem(), getMenu().getDefaultProductionAmount());

      menu.setDesiredProductionStrategyType(productionStrategyType);
      menu.setDesiredProductionBillAmount(
            productionStrategyType.requiresAmount() ? getMenu().getDefaultProductionAmount() : -1);
      replicateStateToServer();
   }

   private void replicateStateToServer() {
      PacketDistributor.sendToServer(
            new EditProductionBillUpdateState(
                  menu.getDesiredProductionStrategyType(),
                  menu.getDesiredProductionBillAmount()));
   }

   @Override
   public void init() {
      super.init();

      changeStrategyButton =
            CycleButton.builder(this::apply)
                  .withValues(ProductionStrategyType.values())
                  .create(
                        contentXEnd - 130,
                        contentYStart + 15,
                        100,
                        18,
                        Component.translatable("production_bill.production_strategy.heading"),
                        this::onChangeStrategyType);

      changeStrategyButton.setValue(
            menu.getExistingBill() != null ? menu.getExistingBill().getProductionStrategy().getType()
                  : ProductionStrategyType.PRODUCE_INFINITE);

      itemQuantitySelectorWidget =
            new ItemQuantitySelectorWidget(
                  contentXEnd - 65,
                  contentYStart + 38,
                  menu.getOutputSlot().getItem(),
                  menu.getDesiredProductionBillAmount(),
                  desiredProductionBillAmount -> {
                     menu.setDesiredProductionBillAmount(desiredProductionBillAmount);
                     replicateStateToServer();
                  });
      itemQuantitySelectorWidget.visible = changeStrategyButton.getValue().requiresAmount();

      addRenderableWidget(changeStrategyButton);
      addRenderableWidget(itemQuantitySelectorWidget);

      menu.getResultContainer().addListener(this);
   }

   public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
      super.render(graphics, mouseX, mouseY, partialTicks);

      if (changeStrategyButton.getValue().requiresAmount()) {
         graphics.drawString(
               font,
               Component.translatable("gui.misc.quantity"),
               contentXEnd - 120,
               contentYStart + 43,
               Colors.MENU_TEXT_DARK,
               false);
      }
   }

   @Override
   public void containerChanged(Container container) {
      if (itemQuantitySelectorWidget == null)
         return;

      if (!menu.getDesiredProductionStrategyType().requiresAmount())
         return;

      ItemStack resultItem = container.getItem(0);
      if (resultItem.is(itemQuantitySelectorWidget.getDisplayItem().getItem()))
         return;

      int resetAmount = menu.getDesiredProductionBillAmount();

      ItemStack newDisplay = container.getItem(0).copy();
      itemQuantitySelectorWidget.setItemAndQuantity(newDisplay, resetAmount);
      menu.setDesiredProductionBillAmount(resetAmount);
      replicateStateToServer();
   }
}
