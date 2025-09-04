package com.uncreated.civilized.core.building;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import com.google.common.collect.ImmutableList;
import com.mojang.logging.LogUtils;
import com.uncreated.civilized.core.building.bounds.BuildingBounds;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;

public abstract class BuildingStore extends SavedData {

   protected static final Logger LOGGER = LogUtils.getLogger();

   protected BuildingDB buildings;

   public abstract Level getLevel();

   protected BuildingStore() {
      buildings = new BuildingDB();
   }

   public ImmutableList<Building> all() {
      return buildings.all();
   }

   public Building createNew(UUID settlementId, UUID placerId, BuildingType buildingType, BuildingBounds bounds) {
      Building building =
            Building.builder()
                  .buildingId(UUID.randomUUID())
                  .settlementId(settlementId)
                  .buildingType(buildingType)
                  .placerId(placerId)
                  .bounds(bounds)
                  .build();

      buildings.add(building);
      setDirty();

      return building;
   }

   public Optional<Building> find(@Nullable UUID buildingId) {
      return buildings.find(buildingId);
   }

   public Set<Building> findForSettlement(@Nullable UUID settlementId) {
      return buildings.getSettlementsToBuildingsIndex().getValues(settlementId);
   }

   public Building get(@Nullable UUID buildingId) {
      return find(buildingId).orElseThrow();
   }

   /**
    * This method has bad performance on large databases.
    */
   @Deprecated()
   public Optional<Building> findEnclosingBuilding(BlockPos blockPos) {
      // BAD IMPLEMENTATION: if the blockpos is on the edge of a a blockpos index "quadrant", it may not be found
      return buildings.getNearbyBlockPosToBuildingsIndex()
            .getValues(blockPos)
            .stream()
            .filter(b -> b.getBounds().contains(blockPos))
            .findFirst();
   }

   /**
    * This method has bad performance on large databases.
    */
   @Deprecated()
   public Optional<Building> findOverlappingBuilding(BuildingBounds bounds) {
      // BAD IMPLEMENTATION: if the blockpos is on the edge of a a blockpos index "quadrant", it may not be found
      return buildings.getNearbyBlockPosToBuildingsIndex()
            .getValues(bounds.getCenter())
            .stream()
            .filter(b -> b.getBounds().isOverlapping(bounds))
            .findFirst();
   }

   public Optional<Building> findStorehouse(UUID settlementId) {
      return findForSettlement(settlementId).stream()
            .filter(b -> b.getBuildingType() == BuildingType.STOREHOUSE)
            .findFirst();
   }
}
