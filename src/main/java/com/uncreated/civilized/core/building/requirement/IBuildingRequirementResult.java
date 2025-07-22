package com.uncreated.civilized.core.building.requirement;

import javax.annotation.Nullable;

import net.minecraft.network.chat.Component;

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
