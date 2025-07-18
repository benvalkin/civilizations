package com.uncreated.civilized.entity.behaviour;

import java.util.Objects;
import java.util.Optional;

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
import com.uncreated.civilized.entity.CivilizedVillager;
import com.uncreated.civilized.core.villagerinfo.VillagerOccupation;

import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;

public class InvalidateImportantLocations extends Behavior<CivilizedVillager> {

   private static final int FREQUENCY_SECONDS = 20;
   private Logger LOGGER = LogUtils.getLogger();

   public InvalidateImportantLocations() {
      super(ImmutableMap.of());
   }

   private long timeStart;

   @Override
   protected boolean checkExtraStartConditions(ServerLevel level, CivilizedVillager villager) {
      return level.getGameTime() - timeStart > 20 * FREQUENCY_SECONDS;
   }

   @Override
   protected void start(ServerLevel level, CivilizedVillager villager, long gameTicks) {
      timeStart = gameTicks;

      VillagerInfo villagerInfo = villager.getInfo();
      VillagerOccupation oldOccupation = villagerInfo.getOccupation();
      Optional<Building> oldHome = ServerBuildingsStore.INSTANCE.find(villagerInfo.getHomeBuildingId());

      Optional<Building> newHome = invalidateHome(villagerInfo, level);
      if (newHome.isPresent()) {
         // TODO: find an alternative for newHome.get().refreshBlockEntities(level);
         villagerInfo.setOccupation(newHome.get().getBuildingType().toJobType());
      } else
         villagerInfo.setOccupation(VillagerOccupation.UNEMPLOYED);

      Optional<Building> worksite = invalidateWorksite(villagerInfo, level);
      if (worksite.isPresent()) {
         villager.getBrain()
               .setMemory(MemoryModuleType.JOB_SITE, new GlobalPos(level.dimension(), worksite.get().getBlockPos()));
      }

      villager.invalidateHomeAndJobMemories();

      boolean jobChanged = oldOccupation != villagerInfo.getOccupation();
      boolean homeChanged =
            !Objects.equals(oldHome.map(Building::getBuildingId).orElse(null), villagerInfo.getHomeBuildingId());
      boolean changed = jobChanged || homeChanged;
      if (changed) {
         ServerVillagerStore.INSTANCE.setDirty();
         ServerVillagerStore.INSTANCE.replicateChange(villagerInfo, StoreOperation.UPDATE);
      }

      if (homeChanged) {
         LOGGER.info(
               "CHANGED: Villager {} changed homes: {} => {}",
               villagerInfo.getFullName(),
               oldHome.isPresent() ? oldHome.get().getBuildingType() : "none",
               newHome.isPresent() ? newHome.get().getBuildingType() : "none");
         oldHome.ifPresent(b -> ServerBuildingsStore.INSTANCE.replicateChange(b, StoreOperation.UPDATE));
         newHome.ifPresent(b -> ServerBuildingsStore.INSTANCE.replicateChange(b, StoreOperation.UPDATE));
      }
      if (jobChanged) {
         LOGGER.info(
               "CHANGED: Villager {} changed jobs: {} => {}",
               villagerInfo.getFullName(),
               oldOccupation,
               villagerInfo.getOccupation());
      }
      LOGGER.info(
            "Villager {} worksite: {} - current activity {} ",
            villagerInfo.getFullName(),
            worksite.isPresent() ? worksite.get().getBuildingType() : "none",
            villager.getBrain().getActiveNonCoreActivity());
   }

   private Optional<Building> invalidateHome(VillagerInfo villagerInfo, ServerLevel level) {
      if (villagerInfo.getHomeBuildingId() == null) {
         // try to find a new home if homeless
         Optional<Building> newHome =
               BuildingUtil.findUnoccupiedHome(
                     villagerInfo.getSettlementId(),
                     ServerBuildingsStore.INSTANCE,
                     ServerVillagerStore.INSTANCE);
         if (newHome.isPresent()) {
            villagerInfo.setHomeBuildingId(newHome.get().getBuildingId());
            return newHome;
         }
         return Optional.empty();
      }

      Optional<Building> home = ServerBuildingsStore.INSTANCE.find(villagerInfo.getHomeBuildingId());
      if (home.isPresent() && home.get().getBuildingType() == BuildingType.TRADING_POST) {
         // try to move villager out of the inn if a better home is available
         Optional<Building> betterHome =
               BuildingUtil.findUnoccupiedHome(
                     villagerInfo.getSettlementId(),
                     ServerBuildingsStore.INSTANCE,
                     ServerVillagerStore.INSTANCE);
         if (betterHome.isPresent()) {
            villagerInfo.setHomeBuildingId(betterHome.get().getBuildingId());
            return betterHome;
         }
      }

      return home;
   }

   private Optional<Building> invalidateWorksite(VillagerInfo villagerInfo, ServerLevel level) {
      if (villagerInfo.getOccupation() == VillagerOccupation.FARMER) {

         Optional<Building> cropFarm =
               ServerBuildingsStore.INSTANCE.all()
                     .stream()
                     .filter(b -> b.getBuildingType() == BuildingType.CROP_FARM)
                     .findFirst();
         return cropFarm;
      }
      return Optional.empty();
   }

   @Override
   protected void stop(ServerLevel level, CivilizedVillager entity, long gameTime) {

   }

   @Override
   protected boolean canStillUse(ServerLevel level, CivilizedVillager entity, long gameTime) {
      return false;
   }
}
