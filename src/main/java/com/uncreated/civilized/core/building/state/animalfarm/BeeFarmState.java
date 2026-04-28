package com.uncreated.civilized.core.building.state.animalfarm;

import com.uncreated.civilized.core.building.Building;

import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class BeeFarmState extends AnimalFarmState {
   public BeeFarmState(Building building) {
      super(building);
   }

   @Override
   public void tryApplyDefaults(ItemStack[] foodSlots) {
      foodSlots[0] = new ItemStack(Items.POPPY);
      foodSlots[1] = new ItemStack(Items.DANDELION);
      foodSlots[2] = new ItemStack(Items.OXEYE_DAISY);
   }

   @Override
   public boolean isCorrectAnimalFood(ItemStack itemStack) {
      return itemStack.is(ItemTags.BEE_FOOD);
   }
}
