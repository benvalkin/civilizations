package com.uncreated.civilized.entity.behaviour;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

import org.slf4j.Logger;

import com.google.common.collect.ImmutableMap;
import com.mojang.logging.LogUtils;
import com.uncreated.civilized.core.StoreOperation;
import com.uncreated.civilized.core.building.Building;
import com.uncreated.civilized.core.building.BuildingType;
import com.uncreated.civilized.core.building.ServerBuildingsStore;
import com.uncreated.civilized.core.building.util.BuildingUtil;
import com.uncreated.civilized.core.villagerinfo.ServerVillagerStore;
import com.uncreated.civilized.core.villagerinfo.VillagerInfo;
import com.uncreated.civilized.core.villagerinfo.VillagerOccupation;
import com.uncreated.civilized.entity.CivilizedVillager;
import com.uncreated.civilized.neoforge.registration.ai.AIRegistry;

import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;

public class InvalidateImportantLocations extends RecurringIntervalBehaviour<CivilizedVillager> {

   private Logger LOGGER = LogUtils.getLogger();

   public InvalidateImportantLocations() {
      super(ImmutableMap.of());
   }

   @Override
   protected long getIntervalDurationSeconds() {
      return 20;
   }

   @Override
   protected void start(ServerLevel level, CivilizedVillager villager, long gameTicks) {
      super.start(level, villager, gameTicks);

      VillagerInfo villagerInfo = villager.getInfo();
      VillagerOccupation oldOccupation = villagerInfo.getOccupation();
      Optional<Building> oldHome = ServerBuildingsStore.INSTANCE.find(villagerInfo.getHomeBuildingId());
      Optional<Building> oldWorksite = ServerBuildingsStore.INSTANCE.find(villagerInfo.getPrimaryWorksiteId());

      Optional<Building> home = invalidateHome(villagerInfo, level);
      if (home.isPresent()) {
         villagerInfo.setOccupation(home.get().getBuildingType().getOccupation());
         villagerInfo.setHomeBuildingId(home.get().getBuildingId());
         villager.getBrain()
               .setMemory(MemoryModuleType.HOME, new GlobalPos(level.dimension(), home.get().getBlockPos()));

         if (villagerInfo.getOccupation() != VillagerOccupation.UNEMPLOYED) // take care not to make villagers think
                                                                            // they can work if they are unemployed
            villager.getBrain()
                  .setMemory(AIRegistry.MM_VILLAGER_WORKTIME_OCCUPATION.get(), villagerInfo.getOccupation());
      } else {
         villagerInfo.setOccupation(VillagerOccupation.UNEMPLOYED);
         villager.getBrain().eraseMemory(MemoryModuleType.HOME);
         villager.getBrain().eraseMemory(AIRegistry.MM_VILLAGER_WORKTIME_OCCUPATION.get());
      }

      Optional<Building> worksite = invalidateWorksite(villagerInfo, level);
      if (worksite.isPresent()) {
         villagerInfo.setPrimaryWorksiteId(worksite.get().getBuildingId());
         villager.getBrain()
               .setMemory(MemoryModuleType.JOB_SITE, new GlobalPos(level.dimension(), worksite.get().getBlockPos()));
      } else {
         villagerInfo.setPrimaryWorksiteId(null);
         villager.getBrain().eraseMemory(MemoryModuleType.JOB_SITE);
      }

      boolean jobChanged = oldOccupation != villagerInfo.getOccupation();
      boolean homeChanged =
            !Objects.equals(oldHome.map(Building::getBuildingId).orElse(null), villagerInfo.getHomeBuildingId());
      boolean worksiteChanged =
            !Objects.equals(oldWorksite.map(Building::getBuildingId).orElse(null), villagerInfo.getPrimaryWorksiteId());
      boolean villagerChanged = jobChanged || homeChanged || worksiteChanged;
      if (villagerChanged) {
         ServerVillagerStore.INSTANCE.setDirty();
         ServerVillagerStore.INSTANCE.replicateChange(villagerInfo, StoreOperation.UPDATE);
      }

      if (homeChanged) {
         oldHome.ifPresent(b -> ServerBuildingsStore.INSTANCE.replicateChange(b, StoreOperation.UPDATE));
         home.ifPresent(b -> ServerBuildingsStore.INSTANCE.replicateChange(b, StoreOperation.UPDATE));
      }
      if (jobChanged) {
         villager.refreshBrain(level);
         villager.updateClothing();
      }
      if (worksiteChanged) {
         oldWorksite.ifPresent(b -> ServerBuildingsStore.INSTANCE.replicateChange(b, StoreOperation.UPDATE));
         worksite.ifPresent(b -> ServerBuildingsStore.INSTANCE.replicateChange(b, StoreOperation.UPDATE));
      }
   }

   private Optional<Building> invalidateHome(VillagerInfo villagerInfo, ServerLevel level) {

      Optional<Building> currentHome = ServerBuildingsStore.INSTANCE.find(villagerInfo.getHomeBuildingId());
      if (currentHome.isPresent()) {
         // try to move villager out of the inn if a better home is available
         if (currentHome.get().getBuildingType() == BuildingType.INN) {
            Optional<Building> betterHome =
                  BuildingUtil.findUnoccupiedHome(
                        villagerInfo.getSettlementId(),
                        ServerBuildingsStore.INSTANCE,
                        ServerVillagerStore.INSTANCE,
                        false);

            if (betterHome.isPresent())
               return betterHome;
         }

         return currentHome; // otherwise, they stay where they are
      }

      return BuildingUtil.findUnoccupiedHome(
            villagerInfo.getSettlementId(),
            ServerBuildingsStore.INSTANCE,
            ServerVillagerStore.INSTANCE,
            true);
   }

   private Optional<Building> invalidateWorksite(VillagerInfo villagerInfo, ServerLevel level) {

      Optional<Building> currentWorksite = ServerBuildingsStore.INSTANCE.find(villagerInfo.getPrimaryWorksiteId());
      if (currentWorksite.isPresent())
         return currentWorksite;

      Predicate<BuildingType> filter;
      if (villagerInfo.getOccupation() == VillagerOccupation.FARMER) {
         filter = b -> b == BuildingType.CROP_FARM;
      } else if (villagerInfo.getOccupation() == VillagerOccupation.WOODCUTTER) {
         filter = b -> b == BuildingType.GROVE;
      } else if (villagerInfo.getOccupation() == VillagerOccupation.MINER) {
         filter = b -> b == BuildingType.MINE;
      } else if (villagerInfo.getOccupation() == VillagerOccupation.RANCHER) {
         filter =
               b -> b == BuildingType.CATTLE_FARM || b == BuildingType.CHICKEN_FARM || b == BuildingType.SHEEP_FARM
                     || b == BuildingType.HOG_FARM;
      } else
         return Optional.empty();

      return BuildingUtil.findUnoccupiedWorksite(
            villagerInfo.getSettlementId(),
            filter,
            ServerBuildingsStore.INSTANCE,
            ServerVillagerStore.INSTANCE);
   }
}
