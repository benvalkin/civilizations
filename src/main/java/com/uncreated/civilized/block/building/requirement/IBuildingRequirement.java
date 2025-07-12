package com.uncreated.civilized.block.building.requirement;

public interface IBuildingRequirement {
   default boolean hideIfSatisfied() {
      return false;
   }
}
