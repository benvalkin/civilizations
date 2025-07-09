package com.uncreated.civilized.ui;

import com.uncreated.civilized.neoforge.registration.BlockRegistry;
import com.uncreated.civilized.neoforge.registration.gui.GuiRegistry;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

public class MyMenu extends AbstractContainerMenu {
   private final Inventory playerInv;
   private final FriendlyByteBuf extraData;
   private final ContainerLevelAccess access;

   // client constructor
   public MyMenu(int containerId, Inventory playerInv, FriendlyByteBuf extraData) {
      this(
            containerId,
            playerInv,
            extraData,
            ContainerLevelAccess.NULL,
            new ItemStackHandler(5),
            DataSlot.standalone());
   }

   // server constructor
   public MyMenu(
         int containerId,
         Inventory playerInv,
         FriendlyByteBuf extraData,
         ContainerLevelAccess access,
         IItemHandler dataInventory,
         DataSlot dataSingle) {
      super(GuiRegistry.MY_MENU.get(), containerId);
      // ...
      this.playerInv = playerInv;
      this.extraData = extraData;
      this.access = access;

      // Check if the data inventory size is some fixed value
      // Then, add slots for data inventory
      this.addSlot(new SlotItemHandler(dataInventory, 0, 0, 0));

      // Add slots for player inventory
      this.addSlot(new Slot(playerInv, 0, 5, 5));

      // Add data slots for handled integers
      this.addDataSlot(dataSingle);
   }

   @Override
   public ItemStack quickMoveStack(Player player, int quickMovedSlotIndex) {
      // The quick moved slot stack
      ItemStack quickMovedStack = ItemStack.EMPTY;
      // The quick moved slot
      Slot quickMovedSlot = this.slots.get(quickMovedSlotIndex);

      // If the slot is in the valid range and the slot is not empty
      if (quickMovedSlot != null && quickMovedSlot.hasItem()) {
         // Get the raw stack to move
         ItemStack rawStack = quickMovedSlot.getItem();
         // Set the slot stack to a copy of the raw stack
         quickMovedStack = rawStack.copy();

         /*
          * The following quick move logic can be simplified to if in data inventory, try to move to player
          * inventory/hotbar and vice versa for containers that cannot transform data (e.g. chests).
          */

         // If the quick move was performed on the data inventory result slot
         if (quickMovedSlotIndex == 0) {
            // Try to move the result slot into the player inventory/hotbar
            if (!this.moveItemStackTo(rawStack, 5, 41, true)) {
               // If cannot move, no longer quick move
               return ItemStack.EMPTY;
            }

            // Perform logic on result slot quick move
            quickMovedSlot.onQuickCraft(rawStack, quickMovedStack);
         }
         // Else if the quick move was performed on the player inventory or hotbar slot
         else if (quickMovedSlotIndex >= 5 && quickMovedSlotIndex < 41) {
            // Try to move the inventory/hotbar slot into the data inventory input slots
            if (!this.moveItemStackTo(rawStack, 1, 5, false)) {
               // If cannot move and in player inventory slot, try to move to hotbar
               if (quickMovedSlotIndex < 32) {
                  if (!this.moveItemStackTo(rawStack, 32, 41, false)) {
                     // If cannot move, no longer quick move
                     return ItemStack.EMPTY;
                  }
               }
               // Else try to move hotbar into player inventory slot
               else if (!this.moveItemStackTo(rawStack, 5, 32, false)) {
                  // If cannot move, no longer quick move
                  return ItemStack.EMPTY;
               }
            }
         }
         // Else if the quick move was performed on the data inventory input slots, try to move to player
         // inventory/hotbar
         else if (!this.moveItemStackTo(rawStack, 5, 41, false)) {
            // If cannot move, no longer quick move
            return ItemStack.EMPTY;
         }

         if (rawStack.isEmpty()) {
            // If the raw stack has completely moved out of the slot, set the slot to the empty stack
            quickMovedSlot.set(ItemStack.EMPTY);
         } else {
            // Otherwise, notify the slot that that the stack count has changed
            quickMovedSlot.setChanged();
         }

         /*
          * The following if statement and Slot#onTake call can be removed if the menu does not represent a container
          * that can transform stacks (e.g. chests).
          */
         if (rawStack.getCount() == quickMovedStack.getCount()) {
            // If the raw stack was not able to be moved to another slot, no longer quick move
            return ItemStack.EMPTY;
         }
         // Execute logic on what to do post move with the remaining stack
         quickMovedSlot.onTake(player, rawStack);
      }

      return quickMovedStack; // Return the slot stack
   }

   @Override
   public boolean stillValid(Player player) {
      return AbstractContainerMenu.stillValid(this.access, player, BlockRegistry.CROP_FARM_BLOCK.get());
   }
}
