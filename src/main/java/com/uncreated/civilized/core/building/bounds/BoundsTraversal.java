package com.uncreated.civilized.core.building.bounds;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.core.BlockPos;

public class BoundsTraversal {

   @Getter(AccessLevel.PUBLIC)
   @Setter(AccessLevel.PACKAGE)
   private BlockPos.MutableBlockPos currentBlockPos;
   private boolean shouldSkipToNextXZ;
   private boolean shouldTerminate;

   BoundsTraversal(BlockPos.MutableBlockPos currentBlockPos) {
      this.currentBlockPos = currentBlockPos;
      shouldSkipToNextXZ = false;
      shouldTerminate = false;
   }

   public void skipToNextXZ() {
      shouldSkipToNextXZ = true;
   }

   public void terminate() {
      shouldTerminate = true;
   }

   boolean shouldSkipToNextXZ() {
      return shouldSkipToNextXZ;
   }

   boolean shouldTerminate() {
      return shouldTerminate;
   }

   void resetSkipToNextXZ() {
      shouldSkipToNextXZ = false;
   }
}
