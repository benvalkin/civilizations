package com.uncreated.civilized.ui.menu.building.residence.artisan;

import java.util.List;
import java.util.Optional;

import javax.annotation.Nullable;

import com.uncreated.civilized.core.StoreOperation;
import com.uncreated.civilized.core.building.Building;
import com.uncreated.civilized.core.building.ClientBuildingStore;
import com.uncreated.civilized.core.building.ServerBuildingsStore;
import com.uncreated.civilized.core.building.crafting.bills.ProductionBill;
import com.uncreated.civilized.core.building.crafting.bills.ProductionType;
import com.uncreated.civilized.core.building.crafting.bills.strategy.ProductionStrategyType;
import com.uncreated.civilized.core.building.state.ArtisanHouseState;
import com.uncreated.civilized.core.settlement.ClientSettlementsStore;
import com.uncreated.civilized.core.settlement.Settlement;
import com.uncreated.civilized.neoforge.registration.gui.GuiRegistry;
import com.uncreated.civilized.networking.packets.EditProductionBillUpdateState;
import com.uncreated.civilized.networking.packets.ShowBuildingScreen;
import com.uncreated.civilized.ui.menu.building.item.management.ItemManagementMenu;
import com.uncreated.civilized.ui.menu.item.management.EyedropperSlot;
import com.uncreated.civilized.ui.menu.item.management.ReadonlySlot;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class EditCraftingRecipeMenu extends ItemManagementMenu implements ContainerListener {

   private final int productionBillIndex;
   @Getter
   @Nullable
   private final ProductionBill existingBill;
   @Getter
   private final SimpleContainer resultContainer;
   @Getter
   private final ReadonlySlot outputSlot;

   @Nullable
   private ServerLevel serverLevel;
   @Nullable
   private RecipeHolder<CraftingRecipe> recipe;
   private final boolean isNewBill;

   @Getter
   @Setter
   private int desiredProductionBillAmount;

   @Getter
   @Setter
   private ProductionStrategyType desiredProductionStrategyType;

   @Getter
   @Setter
   private boolean desiredProductionEnabled;

   // client constructor
   public EditCraftingRecipeMenu(int containerId, Inventory playerInventory, FriendlyByteBuf extraDataFromServer) {
      this(
            containerId,
            playerInventory,
            new SimpleContainer(extraDataFromServer.readInt()),
            ClientSettlementsStore.INSTANCE.get(extraDataFromServer.readUUID()),
            ClientBuildingStore.INSTANCE.get(extraDataFromServer.readUUID()),
            extraDataFromServer.readInt(),
            extraDataFromServer.readBoolean());
   }

   public EditCraftingRecipeMenu(
         int containerId,
         Inventory playerInventory,
         Container craftingMenuContainer,
         Settlement settlement,
         Building building,
         int productionBillIndex,
         boolean isNewBill) {
      super(GuiRegistry.CHOOSE_CRAFTING_RECIPE_MENU.get(), containerId);
      this.container = craftingMenuContainer;
      this.resultContainer = new SimpleContainer(1);
      this.settlement = settlement;
      this.building = building;
      this.productionBillIndex = productionBillIndex;
      this.isNewBill = isNewBill;
      if (playerInventory.player.level() instanceof ServerLevel serverLevel) {
         this.serverLevel = serverLevel;
      }

      int slotIndex = 0;
      for (int y = 0; y < 3; y++) {
         for (int x = 0; x < 3; x++) {
            this.addSlot(new EyedropperSlot(craftingMenuContainer, slotIndex, 51 + 18 * x, 20 + 18 * y));
            slotIndex++;
         }
      }

      outputSlot = new ReadonlySlot(resultContainer, 0, 141, 20 + 18);
      this.addSlot(outputSlot);

      ArtisanHouseState artisanHouseState = (ArtisanHouseState) building.getState();

      if (isNewBill) {
         existingBill = null;
         outputSlot.set(ItemStack.EMPTY);

         desiredProductionBillAmount = -1;
         desiredProductionStrategyType = ProductionStrategyType.PRODUCE_INFINITE;
         desiredProductionEnabled = true;
      } else {
         existingBill = artisanHouseState.getProductionBills().get(productionBillIndex);

         List<ItemStack> inputItems = existingBill.getInputItems();
         for (int i = 0; i < inputItems.size(); i++) {
            slots.get(i).set(inputItems.get(i));
         }

         desiredProductionBillAmount = existingBill.getBillAmount();
         desiredProductionStrategyType = existingBill.getProductionStrategy().getType();
         desiredProductionEnabled = existingBill.isEnabled();

         outputSlot.set(existingBill.getDisplayItem());
      }

      this.addStandardInventorySlots(playerInventory, 87, 88);

      this.container.startOpen(playerInventory.player);

      if (serverLevel != null) {
         this.addSlotListener(this);
      }
   }

   @Override
   public void slotsChanged(Container container) {
      super.slotsChanged(container);
   }

   @Override
   public void removed(Player player) {
      super.removed(player);
      this.container.stopOpen(player);

      if (!(player instanceof ServerPlayer serverPlayer))
         return;

      ArtisanHouseState artisanHouseState = (ArtisanHouseState) building.getState();

      if (recipe != null) {

         ProductionBill newBill =
               new ProductionBill(
                     recipe.id().location().getPath(),
                     ProductionType.CRAFTING,
                     desiredProductionStrategyType,
                     desiredProductionBillAmount,
                     desiredProductionEnabled, // note: currently no support for enabling production from this menu yet
                     this.getItems().subList(0, 9),
                     outputSlot.getItem());

         if (isNewBill)
            artisanHouseState.addBill(newBill);
         else {
            artisanHouseState.replaceBill(productionBillIndex, newBill);
         }

         ServerBuildingsStore.INSTANCE.replicateChange(building, StoreOperation.UPDATE);
         ServerBuildingsStore.INSTANCE.setDirty();

         // BAD IMPLEMENTATION: this method is called when the player's menu closes (e.g. when escape is pressed), so
         // this
         // currently re-opens the UI when it shouldn't.
         CompoundTag additionalData = new CompoundTag();
         building.getState().serverAddToBuildingScreenContext(additionalData, serverPlayer.serverLevel());
         PacketDistributor.sendToPlayer(serverPlayer, new ShowBuildingScreen(building.getBuildingId(), additionalData));
      }
   }

   @Override
   public void slotChanged(AbstractContainerMenu craftingContainerMenu, int i, ItemStack itemStack) {

      if (i >= 9) // crafting slots are always the first 9, we don't care about any others
         return;

      computeRecipeAndDisplay(craftingContainerMenu);
   }

   @Override
   public void dataChanged(AbstractContainerMenu abstractContainerMenu, int i, int i1) {

   }

   private void computeRecipeAndDisplay(AbstractContainerMenu craftingContainerMenu) {
      List<ItemStack> inputItems = craftingContainerMenu.getItems().subList(0, 9);

      CraftingInput craftingInput = CraftingInput.of(3, 3, inputItems);

      Optional<RecipeHolder<CraftingRecipe>> recipe =
            serverLevel.recipeAccess()
                  .getRecipeFor(RecipeType.CRAFTING, CraftingInput.of(3, 3, inputItems), serverLevel);

      if (recipe.isEmpty()) {
         outputSlot.set(ItemStack.EMPTY);
         this.recipe = null;
         return;
      }

      this.recipe = recipe.get();
      ItemStack resultItem = recipe.get().value().assemble(craftingInput, serverLevel.registryAccess());

      outputSlot.set(resultItem);
   }

   public static void serverReceiveDesiredProductionBillAmount(
         EditProductionBillUpdateState packet,
         IPayloadContext context) {

      if (!(context.player().containerMenu instanceof EditCraftingRecipeMenu editCraftingRecipeMenu))
         return;

      editCraftingRecipeMenu.setDesiredProductionStrategyType(packet.desiredProductionStrategyType());
      editCraftingRecipeMenu.setDesiredProductionBillAmount(packet.desiredProductionAmount());
   }

   public int getDefaultProductionAmount() {
      return 16;
   }
}
