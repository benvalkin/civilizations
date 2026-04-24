package com.uncreated.civilized.ui.menu.building.residence.artisan;

import javax.annotation.Nullable;

import com.mojang.datafixers.util.Pair;
import com.uncreated.civilized.core.building.production.bills.strategy.ProductionStrategyType;
import com.uncreated.civilized.core.building.state.ArtisanHouseState;
import com.uncreated.civilized.networking.packets.EditProductionBillUpdateState;
import com.uncreated.civilized.ui.components.widget.ItemQuantitySelectorWidget;
import com.uncreated.civilized.ui.menu.building.item.management.ItemManagementScreen;
import com.uncreated.civilized.ui.menu.building.widgets.RecipeAllowedIcon;
import com.uncreated.civilized.ui.style.Colors;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerListener;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;

public abstract class EditRecipeScreen<TEditRecipeMenu extends EditRecipeMenu<?, ?>>
      extends ItemManagementScreen<TEditRecipeMenu> implements ContainerListener {

   private CycleButton<ProductionStrategyType> changeStrategyButton;
   private ItemQuantitySelectorWidget itemQuantitySelectorWidget;

   RecipeAllowedIcon recipeAllowedIcon;

   public EditRecipeScreen(
         ResourceLocation backgroundTexture,
         TEditRecipeMenu menu,
         Inventory playerInventory,
         Component title) {
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

      Pair<Integer, Integer> iconXY = getRecipeAllowedIconXYOffset();
      recipeAllowedIcon =
            new RecipeAllowedIcon(contentXStart + iconXY.getFirst(), contentYStart + iconXY.getSecond(), 18);

      if (menu.getExistingBill() != null)
         // if this menu is showing an existing bill, it must be a valid recipe already
         receiveRecipeAllowed(RecipeAllowed.ALLOWED);
      else
         // blank recipe obviously isn't valid yet
         receiveRecipeAllowed(RecipeAllowed.INVALID_RECIPE);

      addRenderableWidget(changeStrategyButton);
      addRenderableWidget(itemQuantitySelectorWidget);
      addRenderableWidget(recipeAllowedIcon);

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

   public void receiveRecipeAllowed(RecipeAllowed result) {

      @Nullable
      Tooltip helpTooltip = null;
      if (building.getState() instanceof ArtisanHouseState artisanHouseState)
         helpTooltip = artisanHouseState.getAllowedRecipeHelpTooltip();

      switch (result) {
      case ALLOWED:
         done.active = true;
         done.setTooltip(null);
         recipeAllowedIcon.changeStateToAllowed(helpTooltip);
         break;
      case NOT_ALLOWED:
         done.active = false;
         done.setTooltip(
               Tooltip.create(
                     Component.translatable(
                           "menu.building.residence.production_bills.edit_recipe.done.tooltip.recipe_not_allowed")));
         recipeAllowedIcon.changeStateNotAllowed(helpTooltip);
         break;
      case INVALID_RECIPE:
         done.active = false;
         done.setTooltip(
               Tooltip.create(
                     Component.translatable(
                           "menu.building.residence.production_bills.edit_recipe.done.tooltip.invalid_recipe")));
         recipeAllowedIcon.changeStateToInvalid();
         break;
      }
   }

   protected Pair<Integer, Integer> getRecipeAllowedIconXYOffset() {
      return Pair.of(86, 35);
   }
}
