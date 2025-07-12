package com.uncreated.civilized.core.building;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.server.level.ServerLevel;
import org.slf4j.Logger;

import com.google.common.collect.ImmutableList;
import com.mojang.logging.LogUtils;
import com.uncreated.civilized.core.building.bounds.BuildingBounds;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.saveddata.SavedData;

public abstract class BuildingStore extends SavedData {

   protected static final Logger LOGGER = LogUtils.getLogger();

   protected Map<UUID, Building> buildings;

   public abstract Level getLevel();

   protected BuildingStore() {
      buildings = new HashMap<>();
   }

   public ImmutableList<Building> all() {
      return ImmutableList.copyOf(buildings.values());
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

      buildings.put(building.getBuildingId(), building);
      setDirty();
      return building;
   }

   public Optional<Building> find(BlockPos blockPos) {
      return all().stream().filter(b -> b.getBlockPos().equals(blockPos)).findFirst();
   }

   public Building get(BlockPos blockPos) {
      return find(blockPos).orElseThrow();
   }

   public Optional<Building> find(UUID buildingId) {
      return Optional.ofNullable(buildings.get(buildingId));
   }

   public Building get(UUID buildingId) {
      return find(buildingId).orElseThrow();
   }

   public Optional<Building> findEnclosingBuilding(BlockPos blockPos) {
      return all().stream().filter(b -> b.getBounds().contains(blockPos)).findFirst();
   }

   public Optional<Building> findFromPrimarySign(SignBlockEntity serverEntity) {
      return all().stream().filter(b -> serverEntity == b.getPrimarySign((ServerLevel) serverEntity.getLevel())).findFirst();
   }

   public Optional<Building> findOverlappingBuilding(BuildingBounds bounds) {
      return all().stream().filter(b -> b.getBounds().isOverlapping(bounds)).findFirst();
   }

   public Optional<Building> delete(BlockPos blockPos) {
      Optional<Building> removed = Optional.ofNullable(buildings.remove(blockPos));
      setDirty();
      return removed;
   }
}
