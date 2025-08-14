package com.uncreated.civilized.util;

import java.util.Optional;
import java.util.function.Predicate;

import com.uncreated.civilized.core.building.logistics.AggregateItemStack;
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

   public static int transferNicely(
         Container fromContainer,
         Container toContainer,
         Predicate<ItemStack> searchFunction,
         int upTo) {
      int containerSize = fromContainer.getContainerSize();
      int addedSoFar = 0;

      for (int i = 0; i < containerSize; i++) {
         ItemStack item = fromContainer.getItem(i);
         if (!searchFunction.test(item))
            continue;

         ItemStack toAdd = item.copy();
         if (addedSoFar + toAdd.getCount() > upTo)
            toAdd.shrink(addedSoFar + toAdd.getCount() - upTo);

         ItemStack remainder = addItemNicely(toContainer, toAdd);
         ItemStack actuallyAdded = toAdd.copyWithCount(toAdd.getCount() - remainder.getCount());
         addedSoFar += actuallyAdded.getCount();

         item.shrink(actuallyAdded.getCount());
         fromContainer.setItem(i, item);
      }

      return addedSoFar;
   }

   public static Optional<ItemSearchResult> findItem(Container container, Predicate<ItemStack> itemSearch) {

      for (int i = 0 ; i < container.getContainerSize(); i++) {
         ItemStack item = container.getItem(i);
         if (itemSearch.test(item))
            return Optional.of(new ItemSearchResult(item, i));
      }

      return Optional.empty();
   }

   public static AggregateItemStack countItems(Container container, Predicate<ItemStack> itemSearch) {

      AggregateItemStack result = new AggregateItemStack();

      for (int i = 0 ; i < container.getContainerSize(); i++) {
         ItemStack item = container.getItem(i);
         if (itemSearch.test(item))
            result.add(item);
      }

      return result;
   }

   public record ItemSearchResult(ItemStack itemStack, int slot) {}
}
