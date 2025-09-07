package com.uncreated.civilized.core.building.behaviour;

import java.util.Arrays;

import com.uncreated.civilized.core.building.Building;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class AnimalFarmBehaviour extends BuildingBehaviour {

   public static final String FIELD_FOOD_SLOT = "food_slot_";

   private final ItemStack[] foodSlots;

   protected AnimalFarmBehaviour(Building building) {
      super(building);
      foodSlots = new ItemStack[3];
      foodSlots[0] = ItemStack.EMPTY;
      foodSlots[1] = ItemStack.EMPTY;
      foodSlots[2] = ItemStack.EMPTY;
   }

   public void applyNbt(CompoundTag compoundTag) {
      readItemSlot(0, compoundTag);
      readItemSlot(1, compoundTag);
      readItemSlot(2, compoundTag);
   }

   public CompoundTag toNbt() {
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

   public void tryApplyDefaults() {

      if (Arrays.stream(foodSlots).allMatch(ItemStack::isEmpty)) {
         switch (building.getBuildingType()) {
         case CATTLE_FARM:
         case SHEEP_FARM:
            foodSlots[0] = new ItemStack(Items.WHEAT);
            break;
         case HOG_FARM:
            foodSlots[0] = new ItemStack(Items.POTATO);
            foodSlots[1] = new ItemStack(Items.CARROT);
            foodSlots[2] = new ItemStack(Items.BEETROOT);
            break;
         case CHICKEN_FARM:
            foodSlots[0] = new ItemStack(Items.WHEAT_SEEDS);
            foodSlots[1] = new ItemStack(Items.BEETROOT_SEEDS);
            break;
         }
      }
   }

   public boolean isCorrectFood(ItemStack stack) {
      return Arrays.stream(foodSlots).anyMatch(i -> ItemStack.isSameItem(i, stack));
   }

   public ItemStack getFoodSlot(int i) {
      return foodSlots[i];
   }

   public void setFoodSlot(int i, ItemStack itemStack) {
      foodSlots[i] = itemStack;
   }
}
