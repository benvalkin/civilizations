package com.uncreated.civilized.core.building.behaviour;

import java.util.Arrays;

import com.uncreated.civilized.core.building.Building;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class CropFarmBehaviour extends BuildingBehaviour {

   public static final String FIELD_CROP_SLOT = "crop_slot_";

   private final ItemStack[] cropSlots;

   protected CropFarmBehaviour(Building building) {
      super(building);
      cropSlots = new ItemStack[3];
      cropSlots[0] = ItemStack.EMPTY;
      cropSlots[1] = ItemStack.EMPTY;
      cropSlots[2] = ItemStack.EMPTY;
   }

   public void applyNbt(CompoundTag compoundTag) {
      readCropSlot(0, compoundTag);
      readCropSlot(1, compoundTag);
      readCropSlot(2, compoundTag);
   }

   public CompoundTag toNbt() {
      CompoundTag tag = new CompoundTag();
      writeCropSlot(0, tag);
      writeCropSlot(1, tag);
      writeCropSlot(2, tag);
      return tag;
   }

   private void readCropSlot(int i, CompoundTag compoundTag) {
      String tagKey = FIELD_CROP_SLOT + i;
      if (compoundTag.contains(tagKey))
         cropSlots[i] = ItemStack.parseOptional(building.getRegistryAccess(), compoundTag.getCompound(tagKey));
      else
         cropSlots[i] = ItemStack.EMPTY;
   }

   private void writeCropSlot(int i, CompoundTag tag) {
      String tagKey = FIELD_CROP_SLOT + i;
      ItemStack itemStack = cropSlots[i];
      if (itemStack.isEmpty())
         return;

      tag.put(tagKey, cropSlots[i].save(building.getRegistryAccess()));
   }

   public void tryApplyCropDefaults() {
      if (Arrays.stream(cropSlots).allMatch(ItemStack::isEmpty)) {
         cropSlots[0] = new ItemStack(Items.WHEAT_SEEDS);
         cropSlots[1] = new ItemStack(Items.CARROT);
         cropSlots[2] = new ItemStack(Items.POTATO);
      }
   }

   public ItemStack getCropSlot(int i) {
      return cropSlots[i];
   }

   public void setCropSlot(int i, ItemStack itemStack) {
      cropSlots[i] = itemStack;
   }
}
