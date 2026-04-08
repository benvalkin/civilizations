package com.uncreated.civilized.core.building.state;

import java.util.Arrays;

import com.uncreated.civilized.core.building.Building;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class CropFarmState extends BuildingState {

   public static final String FIELD_CROP_SLOT = "crop_slot_";

   private final ItemStack[] cropSlots;

   public static final int NUMBER_OF_CROP_SLOTS = 3;

   protected CropFarmState(Building building) {
      super(building);
      cropSlots = new ItemStack[NUMBER_OF_CROP_SLOTS];
      cropSlots[0] = ItemStack.EMPTY;
      cropSlots[1] = ItemStack.EMPTY;
      cropSlots[2] = ItemStack.EMPTY;
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
      String tagKey = FIELD_CROP_SLOT + i;
      if (compoundTag.contains(tagKey))
         cropSlots[i] = ItemStack.parseOptional(building.getRegistryAccess(), compoundTag.getCompound(tagKey));
      else
         cropSlots[i] = ItemStack.EMPTY;
   }

   private void writeItemSlot(int i, CompoundTag tag) {
      String tagKey = FIELD_CROP_SLOT + i;
      ItemStack itemStack = cropSlots[i];
      if (itemStack.isEmpty())
         return;

      tag.put(tagKey, cropSlots[i].save(building.getRegistryAccess()));
   }

   public void tryApplyDefaults() {
      if (Arrays.stream(cropSlots).allMatch(ItemStack::isEmpty)) {
         cropSlots[0] = new ItemStack(Items.WHEAT_SEEDS);
         cropSlots[1] = new ItemStack(Items.CARROT);
         cropSlots[2] = new ItemStack(Items.POTATO);
      }
   }

   public boolean isCorrectCrop(ItemStack stack) {
      return Arrays.stream(cropSlots).anyMatch(i -> ItemStack.isSameItem(i, stack));
   }

   public ItemStack getCropSlot(int i) {
      return cropSlots[i];
   }

   public void setCropSlot(int i, ItemStack itemStack) {
      cropSlots[i] = itemStack;
   }
}
