package com.uncreated.civilized.core.building.bounds;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.Consumer;

import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.NotNull;

import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;

@Getter
public class BuildingBounds {

   private final Random random = new Random();

   public static BuildingBounds decode(FriendlyByteBuf buffer) {
      return new BuildingBounds(buffer.readBlockPos(), buffer.readBlockPos(), buffer.readBlockPos());
   }

   public void encode(FriendlyByteBuf buffer) {
      buffer.writeBlockPos(center);
      buffer.writeBlockPos(lowerCorner);
      buffer.writeBlockPos(upperCorner);
   }

   private final BlockPos center;
   private final BlockPos lowerCorner;
   private final BlockPos upperCorner;
   private final AABB encapsulatingAABB;

   public BuildingBounds(BlockPos center, BlockPos lowerCorner, BlockPos upperCorner) {
      this.center = center;
      this.lowerCorner = lowerCorner;
      this.upperCorner = upperCorner;
      encapsulatingAABB = AABB.encapsulatingFullBlocks(lowerCorner, upperCorner);
   }

   public static BuildingBounds singleBlock(BlockPos center) {
      return new BuildingBounds(center, center, center);
   }

   public boolean contains(BlockPos pos) {
      return encapsulatingAABB.contains(pos.getCenter());
   }

   public boolean isOverlapping(BuildingBounds other) {
      return encapsulatingAABB.intersects(other.getEncapsulatingAABB());
   }

   public int getDirectionBound(Direction direction) {
      return switch (direction) {
      case EAST -> upperCorner.getX();
      case WEST -> lowerCorner.getX();
      case SOUTH -> upperCorner.getZ();
      case NORTH -> lowerCorner.getZ();
      case UP -> upperCorner.getY();
      case DOWN -> lowerCorner.getY();
      };
   }

   public void traverseBlocksWithin(Consumer<BoundsTraversal> action) {
      BlockPos.MutableBlockPos current = lowerCorner.mutable();

      BoundsTraversal traversal = new BoundsTraversal(current);

      for (int x = lowerCorner.getX(); x <= upperCorner.getX(); x++) {
         for (int z = lowerCorner.getZ(); z <= upperCorner.getZ(); z++) {

            traversal.resetSkipToNextXZ();

            for (int y = lowerCorner.getY(); y <= upperCorner.getY(); y++) {
               traversal.setCurrentBlockPos(current.set(x, y, z));

               action.accept(traversal);

               if (traversal.shouldTerminate())
                  return;

               if (traversal.shouldSkipToNextXZ())
                  break;
            }
         }
      }
   }

   public void traverseBlocksWithinTerminateYChecksIfCanSeeSky(Consumer<BlockPos.MutableBlockPos> action, Level level) {
      BlockPos.MutableBlockPos current = lowerCorner.mutable();

      for (int x = lowerCorner.getX(); x <= upperCorner.getX(); x++) {
         for (int z = lowerCorner.getZ(); z <= upperCorner.getZ(); z++) {
            for (int y = lowerCorner.getY(); y <= upperCorner.getY(); y++) {
               action.accept(current.set(x, y, z));

               if (level.canSeeSky(current))
                  break;
            }
         }
      }
   }

   public void traverseBaseRectangleBlocks(Consumer<BlockPos> action, int y) {
      BlockPos.MutableBlockPos current = lowerCorner.mutable();
      for (int x = lowerCorner.getX(); x <= upperCorner.getY(); x++) {
         for (int z = lowerCorner.getZ(); z <= upperCorner.getZ(); z++) {
            action.accept(current.set(x, y, z));
         }
      }
   }

   public static final int MIN_ROOF_HEIGHT_ABOVE_FLOOR = 3;

   public Set<BlockPos> findValidInsideFloorBlocks(Level level) {
      Set<BlockPos> validFloorPositions = new HashSet<>();
      BlockPos.MutableBlockPos current = lowerCorner.mutable();
      for (int x = lowerCorner.getX(); x <= upperCorner.getX(); x++) {
         for (int z = lowerCorner.getZ(); z <= upperCorner.getZ(); z++) {
            for (int y = lowerCorner.getY(); y <= upperCorner.getY(); y++) {
               current.set(x, y, z);

               if (level.canSeeSky(current)) {
                  break;
               }

               if (blockIsAirAboveFloor(current, level) && blockHasRoofWithEnoughHeight(current, level)) {
                  validFloorPositions.add(current.immutable());
               }
            }
         }
      }
      return validFloorPositions;
   }

   public BlockPos findRandomInsideFloorBlock(Level level) {
      List<BlockPos> floorBlocks = findValidInsideFloorBlocks(level).stream().toList();
      if (floorBlocks.isEmpty())
         return center;

      int randomIndex = random.nextInt(floorBlocks.size());
      return floorBlocks.get(randomIndex);
   }

   public static boolean blockIsAirAboveFloor(BlockPos pos, Level level) {

      if (!level.getBlockState(pos).isAir())
         return false;

      BlockPos.MutableBlockPos below = pos.mutable().move(Direction.DOWN, 1);
      return !level.getBlockState(below).isAir();
   }

   public static boolean blockHasRoofWithEnoughHeight(BlockPos blockAboveFloor, Level level) {

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

   public List<BlockEntity> getBlockEntitiesInsideBuilding(@NotNull Level level) {

      List<BlockEntity> entitiesInside = Lists.newArrayList();
      traverseBlocksWithin(traversal -> {
         BlockEntity entity = level.getBlockEntity(traversal.getCurrentBlockPos());
         if (entity == null)
            return;

         entitiesInside.add(entity);
      });
      return entitiesInside;
   }

   @Override
   public String toString() {
      return String.format("Center: %s LowerCorner: %s UpperCorner: %s", center, lowerCorner, upperCorner);
   }
}
