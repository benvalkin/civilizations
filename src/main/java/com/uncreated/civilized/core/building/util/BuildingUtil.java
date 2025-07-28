package com.uncreated.civilized.core.building.util;

import static com.uncreated.civilized.ui.menu.building.residence.tabs.ManageResidentsTab.MAX_ASSIGNED_RESIDENTS;
import static com.uncreated.civilized.ui.menu.building.worksite.residence.tabs.ManageWorkersTab.MAX_ASSIGNED_WORKERS;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;

import org.jetbrains.annotations.Nullable;

import com.uncreated.civilized.core.building.Building;
import com.uncreated.civilized.core.building.BuildingStore;
import com.uncreated.civilized.core.building.BuildingType;
import com.uncreated.civilized.core.villagerinfo.VillagerInfo;
import com.uncreated.civilized.core.villagerinfo.VillagerStore;

import net.minecraft.core.BlockPos;

public class BuildingUtil {

   public static List<VillagerInfo> getResidents(Building building, VillagerStore store) {
      return store.all().stream().filter(v -> v.isOccupantOf(building)).toList();
   }

   public static List<VillagerInfo> getAssignedWorkers(Building building, VillagerStore store) {
      return store.all().stream().filter(v -> v.isAssignedWorkerOf(building)).toList();
   }

   public static boolean isBuildingFull(Building building, VillagerStore store) {
      return store.all().stream().filter(v -> v.isOccupantOf(building)).count() == MAX_ASSIGNED_RESIDENTS;
   }

   public static boolean isWorksiteFull(Building building, VillagerStore store) {
      return store.all().stream().filter(v -> v.isAssignedWorkerOf(building)).count() == MAX_ASSIGNED_WORKERS;
   }

   public static Optional<Building> findUnoccupiedHome(
         UUID settlementId,
         BuildingStore buildingStore,
         VillagerStore villagerStore,
         boolean includeTemporaryHomes) {

      return buildingStore.all()
            .stream()
            .filter(
                  b -> b.getSettlementId().equals(settlementId)
                        && (includeTemporaryHomes ? b.getBuildingType().isResidence()
                              : b.getBuildingType().isPermanentResidence())
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

   public static Optional<Building> findUnoccupiedWorksite(
         UUID settlementId,
         Predicate<BuildingType> filter,
         BuildingStore buildingStore,
         VillagerStore villagerStore) {

      return buildingStore.all()
            .stream()
            .filter(
                  b -> b.getSettlementId().equals(settlementId) && filter.test(b.getBuildingType())
                        && !isWorksiteFull(b, villagerStore))
            .findFirst();
   }

   public static Optional<Building> findAnyNearbyHome(
         @Nullable UUID settlementId,
         BlockPos pos,
         float radius,
         BuildingStore buildingStore) {
      if (settlementId == null)
         return Optional.empty();

      return buildingStore.all()
            .stream()
            .filter(
                  b -> b.getBlockPos().closerThan(pos, radius) && b.getSettlementId().equals(settlementId)
                        && b.getBuildingType().isResidence())
            .findFirst();
   }

   public static Optional<Building> findAnyBuilding(@Nullable UUID settlementId, BuildingStore buildingStore) {
      if (settlementId == null)
         return Optional.empty();

      return buildingStore.all().stream().filter(b -> b.getSettlementId().equals(settlementId)).findFirst();
   }
}
