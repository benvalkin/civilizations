package com.uncreated.civilized.entity.behaviour.worker.common;

import com.uncreated.civilized.entity.behaviour.MediumDistanceTravelTask;
import org.slf4j.Logger;

import com.google.common.collect.ImmutableMap;
import com.mojang.logging.LogUtils;
import com.uncreated.civilized.core.building.Building;
import com.uncreated.civilized.core.building.ServerBuildingsStore;
import com.uncreated.civilized.entity.CivilizedVillager;
import com.uncreated.civilized.neoforge.registration.ai.AIRegistry;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.phys.Vec3;

public class IdleStrollAroundWorksite extends Behavior<CivilizedVillager> {
   public static final Logger LOGGER = LogUtils.getLogger();
   private final int maxHorizontalDist;
   private final int maxVerticalDist;
   private final float speedModifier;
   private long nextWorkTime;
   private Building workSite;
   private MediumDistanceTravelTask travelHelper;

   public IdleStrollAroundWorksite(int maxHorizontalDist, int maxVerticalDist, float strollSpeedModifier) {
      super(
            ImmutableMap.of(
                  MemoryModuleType.LOOK_TARGET,
                  MemoryStatus.VALUE_ABSENT,
                  MemoryModuleType.WALK_TARGET,
                  MemoryStatus.VALUE_ABSENT,
                  MemoryModuleType.JOB_SITE,
                  MemoryStatus.VALUE_PRESENT,
                  AIRegistry.MM_HAS_NON_IDLE_WORK_TASK.get(),
                  MemoryStatus.VALUE_ABSENT),
            20 * 15);
      this.maxHorizontalDist = maxHorizontalDist;
      this.maxVerticalDist = maxVerticalDist;
      this.speedModifier = strollSpeedModifier;
   }

   @Override
   protected boolean checkExtraStartConditions(ServerLevel level, CivilizedVillager villager) {
      return true;
   }

   @Override
   protected void start(ServerLevel level, CivilizedVillager villager, long gameTime) {
      nextWorkTime = gameTime;
      workSite = ServerBuildingsStore.INSTANCE.get(villager.getInfo().getPrimaryWorksiteId());
      travelHelper = new MediumDistanceTravelTask(villager, workSite.getBlockPos(), maxHorizontalDist + 1);
   }

   @Override
   protected boolean canStillUse(ServerLevel level, CivilizedVillager villager, long gameTime) {
      return villager.getBrain().checkMemory(AIRegistry.MM_HAS_NON_IDLE_WORK_TASK.get(), MemoryStatus.VALUE_ABSENT);
   }

   @Override
   protected void tick(ServerLevel level, CivilizedVillager villager, long tickTime) {

      if (!travelHelper.isJourneySuccessful()) {
         travelHelper.walkToPoi(tickTime);
         return;
      }

      if (tickTime >= nextWorkTime) {
         nextWorkTime += villager.getRandom().nextInt(5 * 20, 15 * 20);

         Vec3 wanderPos;
         if (workSite.getBounds().contains(villager.blockPosition())) {
            wanderPos = LandRandomPos.getPos(villager, maxHorizontalDist, maxVerticalDist);
         } else {
            wanderPos =
                  LandRandomPos.getPosTowards(
                        villager,
                        maxHorizontalDist,
                        maxVerticalDist,
                        workSite.getBlockPos().getBottomCenter());
         }

         if (wanderPos != null)
            villager.getBrain().setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(wanderPos, speedModifier, 2));
         else
            villager.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
      }
   }
}
