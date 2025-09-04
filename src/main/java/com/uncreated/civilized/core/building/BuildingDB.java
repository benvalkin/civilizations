package com.uncreated.civilized.core.building;

import java.util.UUID;

import com.uncreated.civilized.core.InMemoryDB;
import com.uncreated.civilized.core.SetIndex;

import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class BuildingDB extends InMemoryDB<UUID, Building> {

   private Level level;
   @Getter
   private final SetIndex<UUID, Building> settlementsToBuildingsIndex = new SetIndex<>();
   @Getter
   private final SetIndex<BlockPos, Building> nearbyBlockPosToBuildingsIndex = new SetIndex<>();

   @Override
   protected UUID getKey(Building obj) {
      return obj.getBuildingId();
   }

   @Override
   protected void index(Building obj) {
      settlementsToBuildingsIndex.add(obj.getSettlementId(), obj);
      nearbyBlockPosToBuildingsIndex.add(computeIndexBlockPos(obj.getBlockPos()), obj);

   }

   @Override
   protected void unindex(Building obj) {
      settlementsToBuildingsIndex.remove(obj.getSettlementId(), obj);
      nearbyBlockPosToBuildingsIndex.remove(computeIndexBlockPos(obj.getBlockPos()), obj);
   }

   public static final int BLOCK_POS_INDEX_WIDTH = 100;

   public BlockPos computeIndexBlockPos(BlockPos blockPos) {
      int indexX = findNearestMultiple(blockPos.getX());
      int indexY = findNearestMultiple(blockPos.getY());
      int indexZ = findNearestMultiple(blockPos.getZ());
      return new BlockPos(indexX, indexY, indexZ);
   }

   public static int findNearestMultiple(int n) {
      int remainder = n % BLOCK_POS_INDEX_WIDTH;

      int lowerMultiple = n - remainder;
      int upperMultiple = n - remainder + BLOCK_POS_INDEX_WIDTH;

      if (Math.abs(n - lowerMultiple) <= Math.abs(n - upperMultiple)) {
         return lowerMultiple;
      } else {
         return upperMultiple;
      }
   }

}
