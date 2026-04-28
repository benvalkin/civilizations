package com.uncreated.civilized.ui.menu.building.worksite.animalfarm.items;

import com.uncreated.civilized.core.building.state.animalfarm.AnimalFarmState;
import com.uncreated.civilized.ui.menu.item.management.EyedropperSlot;

import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;

public class AnimalFoodEyedropperSlot extends EyedropperSlot {
   private final AnimalFarmState buildingState;

   public AnimalFoodEyedropperSlot(AnimalFarmState buildingState, Container container, int slot, int x, int y) {
      super(container, slot, x, y);
      this.buildingState = buildingState;
   }

   @Override
   public boolean mayPlace(ItemStack stack) {
      return buildingState.isCorrectAnimalFood(stack);
   }
}
