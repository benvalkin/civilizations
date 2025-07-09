package com.uncreated.civilized.entity.behaviour;

import java.util.Optional;

import com.uncreated.civilized.entity.CivilizedVillager;

import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.world.entity.ai.behavior.OneShot;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.phys.Vec3;

public class LongDistanceTravelToRememberedPos {
   public static OneShot<CivilizedVillager> create(
         MemoryModuleType<GlobalPos> finalWalkTarget,
         float speedModifier,
         int closeEnoughDistance,
         int tooFarDistance,
         int tooLongUnreachableTicks) {
      return BehaviorBuilder.create(
            instance -> instance
                  .group(
                        instance.registered(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE),
                        instance.absent(MemoryModuleType.WALK_TARGET),
                        instance.present(finalWalkTarget))
                  .apply(
                        instance,
                        (cantReachTargetSince, currentWalkTarget, newWalkTarget) -> (serverLevel, villager, l) -> {

                           GlobalPos globalPos = instance.get(newWalkTarget);
                           Optional<Long> optional = instance.tryGet(cantReachTargetSince);
                           if (globalPos.dimension() == serverLevel.dimension() && (!optional.isPresent()
                                 || serverLevel.getGameTime() - (Long) optional.get() <= tooLongUnreachableTicks)) {
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
                                       // villager.getBrain().eraseMemory(rememberedWalkTarget);
                                       newWalkTarget.erase();
                                       cantReachTargetSince.set(l);
                                       return true;
                                    }
                                 }

                                 currentWalkTarget.set(new WalkTarget(nextIntermediatePos, speedModifier, closeEnoughDistance));
                              } else if (globalPos.pos()
                                    .distManhattan(villager.blockPosition()) > closeEnoughDistance) {
                                 currentWalkTarget
                                       .set(new WalkTarget(globalPos.pos(), speedModifier, closeEnoughDistance));
                              }
                           } else {
                              // villager.getBrain().eraseMemory(rememberedWalkTarget);
                              newWalkTarget.erase();
                              cantReachTargetSince.set(l);
                           }

                           return true;
                        }));
   }
}
