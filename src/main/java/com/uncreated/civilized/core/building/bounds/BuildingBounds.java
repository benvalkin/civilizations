package com.uncreated.civilized.core.building.bounds;

import java.util.List;
import java.util.function.Consumer;

import net.minecraft.core.Direction;
import org.apache.commons.compress.utils.Lists;

import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

@Getter
public class BuildingBounds {

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

   public void traverseBlocksWithin(Consumer<BlockPos> action) {
      BlockPos.MutableBlockPos current = lowerCorner.mutable();

      for (int x = lowerCorner.getX(); x <= upperCorner.getX(); x++) {
         for (int z = lowerCorner.getZ(); z <= upperCorner.getZ(); z++) {
            for (int y = lowerCorner.getY(); y <= upperCorner.getY(); y++) {
               action.accept(current.set(x, y, z));
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

   public List<BlockEntity> getBlockEntitiesInsideBuilding(@NotNull Level level) {
      List<BlockEntity> entitiesInside = Lists.newArrayList();
      traverseBlocksWithin(blockPos -> {
         BlockEntity entity = level.getBlockEntity(blockPos);
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
