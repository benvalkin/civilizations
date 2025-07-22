package com.uncreated.civilized.core.building.util;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.uncreated.civilized.core.building.Building;
import com.uncreated.civilized.core.building.BuildingStore;
import com.uncreated.civilized.core.building.BuildingType;
import com.uncreated.civilized.core.villagerinfo.VillagerInfo;
import com.uncreated.civilized.core.villagerinfo.VillagerStore;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.pattern.BlockPattern;

public class BuildingUtil {

   public static List<VillagerInfo> getOccupants(Building building, VillagerStore store) {
      return store.all().stream().filter(v -> v.isOccupantOf(building)).toList();
   }

   public static boolean isBuildingFull(Building building, VillagerStore store) {
      return store.all().stream().filter(v -> v.isOccupantOf(building)).count() == 2;
   }

   public static Optional<Building> findUnoccupiedHome(
         UUID settlementId,
         BuildingStore buildingStore,
         VillagerStore villagerStore) {

      return buildingStore.all()
            .stream()
            .filter(
                  b -> b.getSettlementId().equals(settlementId) && b.getBuildingType().isPermanentResidence()
                        && !isBuildingFull(b, villagerStore))
            .findFirst();
   }

   public static Optional<Building> findUnoccupiedHome(
         UUID settlementId,
         BuildingType requiredBuildingType,
         BuildingStore buildingStore,
         VillagerStore villagerStore) {

      return buildingStore.all()
            .stream()
            .filter(
                  b -> b.getSettlementId().equals(settlementId) && b.getBuildingType() == requiredBuildingType
                        && !isBuildingFull(b, villagerStore))
            .findFirst();
   }
}
