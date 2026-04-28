package com.uncreated.civilized.ui.menu.building.worksite.animalfarm.items;

import com.uncreated.civilized.core.building.BuildingTypeOld;
import com.uncreated.civilized.ui.menu.item.management.EyedropperSlot;

import net.minecraft.tags.ItemTags;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;

public class AnimalFoodEyedropperSlot extends EyedropperSlot {
   private final BuildingTypeOld buildingType;

   public AnimalFoodEyedropperSlot(BuildingTypeOld buildingType, Container container, int slot, int x, int y) {
      super(container, slot, x, y);
      this.buildingType = buildingType;
   }

   protected boolean isCorrectAnimalFood(ItemStack stack) {
      return switch (buildingType) {
      case CATTLE_FARM -> stack.is(ItemTags.COW_FOOD);
      case SHEEP_FARM -> stack.is(ItemTags.SHEEP_FOOD);
      case PIG_FARM -> stack.is(ItemTags.PIG_FOOD);
      case CHICKEN_FARM -> stack.is(ItemTags.CHICKEN_FOOD);
      default -> throw new IllegalArgumentException();
      };
   }

   @Override
   public boolean mayPlace(ItemStack stack) {
      if (!isCorrectAnimalFood(stack))
         return false;

      return true;
   }
}
