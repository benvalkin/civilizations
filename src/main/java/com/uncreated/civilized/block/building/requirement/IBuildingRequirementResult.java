package com.uncreated.civilized.block.building.requirement;

import net.minecraft.network.chat.Component;

import javax.annotation.Nullable;

public interface IBuildingRequirementResult {
   boolean isSatisfied();

   Component getDescription();

   default @Nullable Component getTooltipDescription() {
      return null;
   }

   default boolean hideIfSatisfied() {
      return false;
   }
}
