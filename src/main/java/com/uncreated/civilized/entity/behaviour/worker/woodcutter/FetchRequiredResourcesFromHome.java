package com.uncreated.civilized.entity.behaviour.worker.woodcutter;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;

import com.google.common.collect.ImmutableMap;
import com.mojang.logging.LogUtils;
import com.uncreated.civilized.core.building.Building;
import com.uncreated.civilized.core.building.ServerBuildingsStore;
import com.uncreated.civilized.entity.CivilizedVillager;
import com.uncreated.civilized.entity.behaviour.MediumDistanceTravelTask;
import com.uncreated.civilized.util.ContainerHelper;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.level.block.entity.ChestBlockEntity;

public class FetchRequiredResourcesFromHome extends Behavior<CivilizedVillager> {

   private static final Logger LOGGER = LogUtils.getLogger();

   private Building storehouse;
   private Building home;
   private List<ChestBlockEntity> chestsAtHome;
   private MediumDistanceTravelTask travelHelper;

   public FetchRequiredResourcesFromHome() {
      super(
            ImmutableMap.of(
                  MemoryModuleType.LOOK_TARGET,
                  MemoryStatus.VALUE_ABSENT,
                  MemoryModuleType.WALK_TARGET,
                  MemoryStatus.VALUE_ABSENT),
            20 * 30);
   }

   @Override
   protected boolean checkExtraStartConditions(ServerLevel level, CivilizedVillager villager) {

      Optional<Building> home = ServerBuildingsStore.INSTANCE.find(villager.getInfo().getHomeBuildingId());
      if (home.isEmpty()) {
         return false;
      }

      List<ChestBlockEntity> chestAtHome =
            home.get()
                  .getBounds()
                  .getBlockEntitiesInsideBuilding(level)
                  .stream()
                  .filter(b -> b instanceof ChestBlockEntity)
                  .map(b -> (ChestBlockEntity) b)
                  .toList();

      if (chestAtHome.isEmpty()) {
         return false;
      }

      this.home = home.get();
      this.chestsAtHome = chestAtHome;
      return true;
   }

   @Override
   protected void start(ServerLevel level, CivilizedVillager villager, long gameTime) {
      LOGGER.info("Villager going to fetch resources.");
      fetched = false;

      travelHelper = new MediumDistanceTravelTask(villager, MemoryModuleType.HOME);
   }

   @Override
   protected void stop(ServerLevel level, CivilizedVillager entity, long gameTime) {
      LOGGER.info("Villager stopped fetching resources.");
   }

   @Override
   protected boolean canStillUse(ServerLevel level, CivilizedVillager entity, long gameTime) {
      return !fetched;
   }

   private boolean fetched = false;

   @Override
   protected void tick(ServerLevel level, CivilizedVillager villager, long tickTime) {

      if (fetched)
         return;

      if (!travelHelper.isJourneySuccessful())
         travelHelper.walkToPoi(tickTime);

      BlockPos chestPos = chestsAtHome.getFirst().getBlockPos();
      // try walk to first chest
      if (!villager.blockPosition().closerThan(chestPos, 2)) {
         if (!villager.getBrain().hasMemoryValue(MemoryModuleType.WALK_TARGET)) {
            villager.getBrain().setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(chestPos, 0.4f, 1));
         }
         return;
      }

      int transferQuota = 64;
      int successfullyTransfered = 0;

      for (var chest : chestsAtHome) {

         successfullyTransfered +=
               ContainerHelper.transferNicely(
                     chest,
                     villager.getWorkInputInventory(),
                     i -> i.is(ItemTags.SAPLINGS),
                     transferQuota - successfullyTransfered);

         if (successfullyTransfered >= transferQuota)
            break;
      }

      fetched = true;
   }
}
