package com.uncreated.civilized.block.building.requirement;

import java.util.HashSet;
import java.util.Set;

import com.uncreated.civilized.core.building.bounds.BuildingBounds;
import com.uncreated.civilized.ui.style.Colors;

import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;

public class SpaceRequirement implements IBuildingRequirement {

   public static final int MIN_ROOF_HEIGHT_ABOVE_FLOOR = 3;
   private final int requiredSpace;

   public SpaceRequirement(int requiredSpace) {
      this.requiredSpace = requiredSpace;
   }

   public Result getResult(Level level, BuildingBounds bounds) {

      Set<ValidFloor> validFloorPositions = new HashSet<>();

      BlockPos.MutableBlockPos current = bounds.getLowerCorner().mutable();
      for (int x = bounds.getLowerCorner().getX(); x <= bounds.getUpperCorner().getX(); x++) {
         for (int z = bounds.getLowerCorner().getZ(); z <= bounds.getUpperCorner().getZ(); z++) {
            for (int y = bounds.getLowerCorner().getY(); y <= bounds.getUpperCorner().getY(); y++) {
               current.set(x, y, z);

               if (level.canSeeSky(current)) {
                  break;
               }

               if (isAirBlockAboveFloor(current, level) && blockHasRoofWithEnoughHeight(current, level)) {
                  validFloorPositions.add(new ValidFloor(current.immutable()));
               }
            }
         }
      }
      return new Result(validFloorPositions, requiredSpace);
   }

   private static boolean isAirBlockAboveFloor(BlockPos pos, Level level) {

      if (!level.getBlockState(pos).isAir())
         return false;

      BlockPos.MutableBlockPos below = pos.mutable().move(Direction.DOWN, 1);
      return !level.getBlockState(below).isAir();
   }

   private static boolean blockHasRoofWithEnoughHeight(BlockPos blockAboveFloor, Level level) {

      BlockPos.MutableBlockPos current = blockAboveFloor.mutable();

      for (int y = 0; y < MIN_ROOF_HEIGHT_ABOVE_FLOOR; y++) {

         if (level.canSeeSky(current))
            return false;

         if (!level.getBlockState(current).isAir())
            return false;

         current.move(Direction.UP);
      }

      return true;
   }

   public record ValidFloor(BlockPos blockPos) {
   }

   public class Result implements IBuildingRequirementResult {
      @Getter
      private final Set<ValidFloor> validFloorBlocks;
      private final int requiredSpace;

      private Result(Set<ValidFloor> validFloorBlocks, int requiredSpace) {
         this.validFloorBlocks = validFloorBlocks;
         this.requiredSpace = requiredSpace;
      }

      @Override
      public boolean isSatisfied() {
         return validFloorBlocks.size() >= requiredSpace;
      }

      @Override
      public Component getDescription() {
         int numberToDisplay = Math.clamp(validFloorBlocks.size(), 0, requiredSpace);
         return Component
               .translatable("menu.building.management.requirements.space.description", numberToDisplay, requiredSpace)
               .withColor(Colors.MENU_TEXT_DARK);
      }

      @Override
      public Component getTooltipDescription() {
         return Component.translatable("menu.building.management.requirements.space.tooltip", requiredSpace);
      }
   }
}
