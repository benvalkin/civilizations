package com.uncreated.civilized.core.building.state.animalfarm;

import com.uncreated.civilized.core.building.Building;

import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class PigFarmState extends AnimalFarmState {
   public PigFarmState(Building building) {
      super(building);
   }

   @Override
   public void tryApplyDefaults(ItemStack[] foodSlots) {
      foodSlots[0] = new ItemStack(Items.POTATO);
      foodSlots[1] = new ItemStack(Items.CARROT);
      foodSlots[2] = new ItemStack(Items.BEETROOT);
   }

   @Override
   public boolean isCorrectAnimalFood(ItemStack itemStack) {
      return itemStack.is(ItemTags.PIG_FOOD);
   }
}
