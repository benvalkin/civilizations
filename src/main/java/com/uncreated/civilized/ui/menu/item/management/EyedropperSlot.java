package com.uncreated.civilized.ui.menu.item.management;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class EyedropperSlot extends Slot {
   public EyedropperSlot(Container container, int slot, int x, int y) {
      super(container, slot, x, y);
   }

   @Override
   public boolean mayPlace(ItemStack stack) {
      return true;
   }

   @Override
   public boolean mayPickup(Player player) {

      if (!this.getItem().isEmpty()) {
         this.set(ItemStack.EMPTY);
         return false;
      }

      return true;
   }

   @Override
   public ItemStack safeInsert(ItemStack stack, int increment) {
      if (!stack.isEmpty() && this.mayPlace(stack)) {
         this.setByPlayer(stack.copyWithCount(1));
      }
      return stack;
   }
}
