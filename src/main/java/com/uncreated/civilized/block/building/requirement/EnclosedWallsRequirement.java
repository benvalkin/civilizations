package com.uncreated.civilized.block.building.requirement;

import java.util.HashSet;
import java.util.Set;

import com.uncreated.civilized.core.building.bounds.BuildingBounds;
import com.uncreated.civilized.ui.style.Colors;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;

public class EnclosedWallsRequirement implements IBuildingRequirement {

   public static final double VALID_WALL_HEIGHT = 3;
   public static final float PERCENTAGE_VALID_SOLID_WALL = 0.6f;
   private final int allowedFaults;

   public EnclosedWallsRequirement(int allowedFaults) {
      this.allowedFaults = allowedFaults;
   }

   public EnclosedWallsRequirement() {
      this(1);
   }

   public Result getResult(Level level, BuildingBounds bounds, Set<SpaceRequirement.ValidFloor> validFloorBlocks) {

      HashSet<BlockPos> faults = new HashSet<>();
      for (SpaceRequirement.ValidFloor floorBlock : validFloorBlocks) {

         BlockPos floorBlockPos = floorBlock.blockPos();
         findFaultsInWall(floorBlockPos, level, bounds, Direction.NORTH, faults);
         findFaultsInWall(floorBlockPos, level, bounds, Direction.SOUTH, faults);
         findFaultsInWall(floorBlockPos, level, bounds, Direction.EAST, faults);
         findFaultsInWall(floorBlockPos, level, bounds, Direction.WEST, faults);
      }

      return new Result(faults.size(), allowedFaults);
   }

   private static void findFaultsInWall(
         BlockPos airBlockAboveFloor,
         Level level,
         BuildingBounds bounds,
         Direction direction,
         HashSet<BlockPos> problematicFloors) {
      BlockPos.MutableBlockPos current = airBlockAboveFloor.mutable();
      while (bounds.contains(current)) {
         current.move(direction);

         if (level.canSeeSky(current)) {
            problematicFloors.add(current.immutable());
            return;
         }

         if (isWall(current, level))
            return;
      }

      problematicFloors.add(current.immutable());
   }

   private static boolean isWall(BlockPos airBlockAboveFloor, Level level) {
      int wallBlocksFound = 0;
      BlockPos.MutableBlockPos current = airBlockAboveFloor.mutable();
      for (int y = 0; y < VALID_WALL_HEIGHT; y++) {
         // it's still fine to use isSolid() for this use case as this method works for chests/doors/panes
         if (level.getBlockState(current).isSolid())
            wallBlocksFound++;

         current.move(Direction.UP);
      }

      return wallBlocksFound / (float) VALID_WALL_HEIGHT > PERCENTAGE_VALID_SOLID_WALL;
   }

   public class Result implements IBuildingRequirementResult {
      private final int actualFaults;
      private final int allowedFaults;

      private Result(int actualFaults, int allowedFaults) {
         this.actualFaults = actualFaults;
         this.allowedFaults = allowedFaults;
      }

      @Override
      public boolean isSatisfied() {
         return actualFaults <= allowedFaults;
      }

      @Override
      public Component getDescription() {
         return Component.translatable("menu.building.management.requirements.enclosed_walls.description", actualFaults)
               .withColor(Colors.MENU_TEXT_DARK);
      }

      @Override
      public Component getTooltipDescription() {
         return Component.translatable("menu.building.management.requirements.enclosed_walls.tooltip");
      }

      @Override
      public boolean hideIfSatisfied() {
         return true;
      }
   }
}
