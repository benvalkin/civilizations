package com.uncreated.civilized.util;

import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;

public class ContainerHelper {
   public static ItemStack addItemNicely(Container container, ItemStack stack) {
        if (stack.isEmpty()) {
            return ItemStack.EMPTY;
        } else {
            ItemStack itemstack = stack.copy();
            moveItemToOccupiedSlotsWithSameType(container, itemstack);
            if (itemstack.isEmpty()) {
                return ItemStack.EMPTY;
            } else {
                moveItemToEmptySlots(container, itemstack);
                return itemstack.isEmpty() ? ItemStack.EMPTY : itemstack;
            }
        }
    }

    private static void moveItemToEmptySlots(Container container, ItemStack stack) {
        for (int i = 0; i < container.getContainerSize(); ++i) {
            ItemStack itemstack = container.getItem(i);
            if (itemstack.isEmpty()) {
                container.setItem(i, stack.copyAndClear());
                container.setChanged();
                return;
            }
        }

    }

    private static void moveItemToOccupiedSlotsWithSameType(Container container, ItemStack stack) {
        for (int i = 0; i < container.getContainerSize(); ++i) {
            ItemStack itemstack = container.getItem(i);
            if (ItemStack.isSameItemSameComponents(itemstack, stack)) {
                moveItemsBetweenStacks(container, stack, itemstack);
                if (stack.isEmpty()) {
                    return;
                }
            }
        }

    }

    private static void moveItemsBetweenStacks(Container container, ItemStack stack, ItemStack other) {
        int i = container.getMaxStackSize(other);
        int j = Math.min(stack.getCount(), i - other.getCount());
        if (j > 0) {
            other.grow(j);
            stack.shrink(j);
            container.setChanged();
        }

    }
}
