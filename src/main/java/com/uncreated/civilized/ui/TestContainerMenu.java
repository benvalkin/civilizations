package com.uncreated.civilized.ui;

import com.uncreated.civilized.neoforge.registration.gui.GuiRegistry;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class TestContainerMenu extends AbstractContainerMenu {
   private static final int SLOT_COUNT = 9;
   private static final int INV_SLOT_START = 9;
   private static final int INV_SLOT_END = 36;
   private static final int USE_ROW_SLOT_START = 36;
   private static final int USE_ROW_SLOT_END = 45;
   private final Container container;

   public TestContainerMenu(int containerId, Inventory playerInventory) {
      this(containerId, playerInventory, new SimpleContainer(9));
   }

   public TestContainerMenu(int containerId, Inventory playerInventory, Container container) {
      super(GuiRegistry.TEST_CONTAINER_MENU.get(), containerId);
      checkContainerSize(container, 9);
      this.container = container;
      container.startOpen(playerInventory.player);
      this.add3x3GridSlots(container, 62, 17);
      this.addStandardInventorySlots(playerInventory, 8, 84);
   }

   protected void add3x3GridSlots(Container container, int x, int y) {
      for (int i = 0; i < 3; ++i) {
         for (int j = 0; j < 3; ++j) {
            int k = j + i * 3;
            this.addSlot(new Slot(container, k, x + j * 18, y + i * 18));
         }
      }

   }

   public boolean stillValid(Player player) {
      return this.container.stillValid(player);
   }

   public ItemStack quickMoveStack(Player player, int index) {
      ItemStack itemstack = ItemStack.EMPTY;
      Slot slot = (Slot) this.slots.get(index);
      if (slot != null && slot.hasItem()) {
         ItemStack itemstack1 = slot.getItem();
         itemstack = itemstack1.copy();
         if (index < 9) {
            if (!this.moveItemStackTo(itemstack1, 9, 45, true)) {
               return ItemStack.EMPTY;
            }
         } else if (!this.moveItemStackTo(itemstack1, 0, 9, false)) {
            return ItemStack.EMPTY;
         }

         if (itemstack1.isEmpty()) {
            slot.setByPlayer(ItemStack.EMPTY);
         } else {
            slot.setChanged();
         }

         if (itemstack1.getCount() == itemstack.getCount()) {
            return ItemStack.EMPTY;
         }

         slot.onTake(player, itemstack1);
      }

      return itemstack;
   }

   public void removed(Player player) {
      super.removed(player);
      this.container.stopOpen(player);
   }
}
