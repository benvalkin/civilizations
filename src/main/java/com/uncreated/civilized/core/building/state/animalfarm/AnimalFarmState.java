package com.uncreated.civilized.core.building.state.animalfarm;

import java.util.Arrays;

import com.uncreated.civilized.core.building.Building;
import com.uncreated.civilized.core.building.state.BuildingState;
import com.uncreated.civilized.core.building.state.IItemManagementMenuProvider;
import com.uncreated.civilized.core.settlement.Settlement;
import com.uncreated.civilized.ui.menu.building.item.management.ItemManagementMenu;
import com.uncreated.civilized.ui.menu.building.worksite.animalfarm.items.ChooseAnimalFoodMenu;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

public abstract class AnimalFarmState extends BuildingState implements IItemManagementMenuProvider {

   public static final String FIELD_FOOD_SLOT = "food_slot_";

   private final ItemStack[] foodSlots;

   public static final int NUMBER_OF_FOOD_SLOTS = 3;

   public AnimalFarmState(Building building) {
      super(building);
      foodSlots = new ItemStack[NUMBER_OF_FOOD_SLOTS];
      foodSlots[0] = ItemStack.EMPTY;
      foodSlots[1] = ItemStack.EMPTY;
      foodSlots[2] = ItemStack.EMPTY;
   }

   public void applyNbt(CompoundTag compoundTag, HolderLookup.Provider registryAccess) {
      readItemSlot(0, compoundTag);
      readItemSlot(1, compoundTag);
      readItemSlot(2, compoundTag);
   }

   public CompoundTag toNbt(HolderLookup.Provider registryAccess) {
      CompoundTag tag = new CompoundTag();
      writeItemSlot(0, tag);
      writeItemSlot(1, tag);
      writeItemSlot(2, tag);
      return tag;
   }

   private void readItemSlot(int i, CompoundTag compoundTag) {
      String tagKey = FIELD_FOOD_SLOT + i;
      if (compoundTag.contains(tagKey))
         foodSlots[i] = ItemStack.parseOptional(building.getRegistryAccess(), compoundTag.getCompound(tagKey));
      else
         foodSlots[i] = ItemStack.EMPTY;
   }

   private void writeItemSlot(int i, CompoundTag tag) {
      String tagKey = FIELD_FOOD_SLOT + i;
      ItemStack itemStack = foodSlots[i];
      if (itemStack.isEmpty())
         return;

      tag.put(tagKey, foodSlots[i].save(building.getRegistryAccess()));
   }

   public final void tryApplyDefaults() {
      tryApplyDefaults(foodSlots);
   }

   public abstract void tryApplyDefaults(ItemStack[] foodSlots);

   public boolean isCorrectFood(ItemStack stack) {
      return Arrays.stream(foodSlots).anyMatch(i -> ItemStack.isSameItem(i, stack));
   }

   public ItemStack getFoodSlot(int i) {
      return foodSlots[i];
   }

   public void setFoodSlot(int i, ItemStack itemStack) {
      foodSlots[i] = itemStack;
   }

   @Override
   public ItemManagementMenu createItemManagementMenu(
         Integer containerId,
         Inventory playerInventory,
         Building building,
         Settlement settlement) {
      return new ChooseAnimalFoodMenu(containerId, playerInventory, new SimpleContainer(3), settlement, building);
   }

   public abstract boolean isCorrectAnimalFood(ItemStack itemStack);
}
