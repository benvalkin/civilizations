package com.uncreated.civilized.entity.behaviour;

import java.util.Optional;

import com.uncreated.civilized.entity.CivilizedVillager;

import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class MediumDistanceTravelTask {

   private static final int CHECK_INTERVAL_SECONDS = 5;

   private CivilizedVillager villager;
   private MemoryModuleType<GlobalPos> finalDestination;
   private float speedModifier;
   private int closeEnoughDistance;
   private long tooFarDistance;
   private long tooLongUnreachableTicks;

   private GlobalPos globalPos;
   private long lastCheckTime;
   @Getter
   private boolean journeySuccessful;

   public MediumDistanceTravelTask(
         CivilizedVillager villager,
         MemoryModuleType<GlobalPos> finalDestination,
         float speedModifier,
         int closeEnoughDistance,
         long tooFarDistance,
         long tooLongUnreachableTicks) {
      this.villager = villager;
      this.finalDestination = finalDestination;
      this.speedModifier = speedModifier;
      this.closeEnoughDistance = closeEnoughDistance;
      this.tooFarDistance = tooFarDistance;
      this.tooLongUnreachableTicks = tooLongUnreachableTicks;
      this.globalPos = villager.getBrain().getMemory(finalDestination).orElseThrow();
   }

   public MediumDistanceTravelTask(CivilizedVillager villager,
                                   MemoryModuleType<GlobalPos> finalDestination) {
      this(villager, finalDestination, 0.4f, 1, 300, 1500);
   }

   protected Level getServerLevel() {
      return villager.level();
   }

   protected boolean closeEnoughToPoi() {
      return globalPos.pos().distManhattan(villager.blockPosition()) > closeEnoughDistance;
   }

   public void walkToPoi(long gameTicks) {

      if (journeySuccessful)
         return;

      if (closeEnoughToPoi()) {
         journeySuccessful = true;
      }

      if (gameTicks - lastCheckTime < 20 * CHECK_INTERVAL_SECONDS)
         return;

      lastCheckTime = gameTicks;

      Optional<Long> optional = villager.getBrain().getMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
      if (globalPos.dimension() == getServerLevel().dimension() && (!optional.isPresent()
            || getServerLevel().getGameTime() - (Long) optional.get() <= tooLongUnreachableTicks)) {
         if (globalPos.pos().distManhattan(villager.blockPosition()) > tooFarDistance) {
            Vec3 nextIntermediatePos = null;
            int attempts = 0;

            while (nextIntermediatePos == null || BlockPos.containing(nextIntermediatePos)
                  .distManhattan(villager.blockPosition()) > tooFarDistance) {
               nextIntermediatePos =
                     DefaultRandomPos.getPosTowards(
                           villager,
                           15,
                           7,
                           Vec3.atBottomCenterOf(globalPos.pos()),
                           (float) (Math.PI / 2));
               if (++attempts == 1000) {
                  villager.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
                  villager.getBrain().setMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, gameTicks);
                  return;
               }
            }

            villager.getBrain()
                  .setMemory(
                        MemoryModuleType.WALK_TARGET,
                        new WalkTarget(nextIntermediatePos, speedModifier, closeEnoughDistance));
         } else if (closeEnoughToPoi()) {
            villager.getBrain()
                  .setMemory(
                        MemoryModuleType.WALK_TARGET,
                        new WalkTarget(globalPos.pos(), speedModifier, closeEnoughDistance));
         }
      } else {
         villager.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
         villager.getBrain().setMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, gameTicks);
      }
   }
}
