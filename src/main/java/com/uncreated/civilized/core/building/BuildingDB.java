package com.uncreated.civilized.core.building;

import java.util.UUID;

import com.uncreated.civilized.core.ChunkIndex;
import com.uncreated.civilized.core.InMemoryDB;
import com.uncreated.civilized.core.SetIndex;

import lombok.Getter;
import net.minecraft.world.level.Level;

public class BuildingDB extends InMemoryDB<UUID, Building> {

   private Level level;
   @Getter
   private final SetIndex<UUID, Building> settlementsToBuildingsIndex = new SetIndex<>();
   @Getter
   private final ChunkIndex<Building> chunkIndex = new ChunkIndex<>();

   @Override
   protected UUID getKey(Building obj) {
      return obj.getBuildingId();
   }

   @Override
   protected void index(Building obj) {
      settlementsToBuildingsIndex.add(obj.getSettlementId(), obj);
      chunkIndex.add(obj.getBlockPos(), obj);
   }

   @Override
   protected void unindex(Building obj) {
      settlementsToBuildingsIndex.remove(obj.getSettlementId(), obj);
      chunkIndex.remove(obj.getBlockPos(), obj);
   }
}
