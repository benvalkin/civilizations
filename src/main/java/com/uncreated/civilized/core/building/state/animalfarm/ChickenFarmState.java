package com.uncreated.civilized.core.building.state.animalfarm;

import com.uncreated.civilized.core.building.Building;

import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class ChickenFarmState extends AnimalFarmState {
   public ChickenFarmState(Building building) {
      super(building);
   }

   @Override
   public void tryApplyDefaults(ItemStack[] foodSlots) {
      foodSlots[0] = new ItemStack(Items.WHEAT_SEEDS);
      foodSlots[1] = new ItemStack(Items.BEETROOT_SEEDS);
   }

   @Override
   public boolean isCorrectAnimalFood(ItemStack itemStack) {
      return itemStack.is(ItemTags.CHICKEN_FOOD);
   }
}
