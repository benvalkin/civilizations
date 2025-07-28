package com.uncreated.civilized.entity.pathfinding;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.pathfinder.PathfindingContext;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;

public class VillagerWalkNodeEvaluator extends WalkNodeEvaluator {

   @Override
   public PathType getPathType(PathfindingContext p_330217_, int p_326856_, int p_326857_, int p_326859_) {
      return getPathTypeStatic(p_330217_, new BlockPos.MutableBlockPos(p_326856_, p_326857_, p_326859_));
   }

   public static PathType getPathTypeStatic(PathfindingContext context, BlockPos.MutableBlockPos pos) {
      int i = pos.getX();
      int j = pos.getY();
      int k = pos.getZ();
      PathType pathtype = context.getPathTypeFromState(i, j, k);

      // === KEY DIFFERENCE ===
      // we forcibly make fence blocks pathfindable
      BlockState blockState = context.getBlockState(pos);
      if (blockState.is(BlockTags.FENCE_GATES, b -> b.getBlock() instanceof FenceGateBlock)) {
         if (blockState.getValue(FenceGateBlock.OPEN))
            pathtype = PathType.DOOR_OPEN;
         else
            pathtype = PathType.DOOR_WOOD_CLOSED;
      }

      // the rest of this method is the same as the super method
      if (pathtype == PathType.OPEN && j >= context.level().getMinY() + 1) {
         PathType var10000;
         switch (context.getPathTypeFromState(i, j - 1, k)) {
         case OPEN:
         case WATER:
         case LAVA:
         case WALKABLE:
            var10000 = PathType.OPEN;
            break;
         case DAMAGE_FIRE:
            var10000 = PathType.DAMAGE_FIRE;
            break;
         case DAMAGE_OTHER:
            var10000 = PathType.DAMAGE_OTHER;
            break;
         case STICKY_HONEY:
            var10000 = PathType.STICKY_HONEY;
            break;
         case POWDER_SNOW:
            var10000 = PathType.DANGER_POWDER_SNOW;
            break;
         case DAMAGE_CAUTIOUS:
            var10000 = PathType.DAMAGE_CAUTIOUS;
            break;
         case TRAPDOOR:
            var10000 = PathType.DANGER_TRAPDOOR;
            break;
         default:
            var10000 = checkNeighbourBlocks(context, i, j, k, PathType.WALKABLE);
         }

         return var10000;
      } else {
         return pathtype;
      }
   }
}
