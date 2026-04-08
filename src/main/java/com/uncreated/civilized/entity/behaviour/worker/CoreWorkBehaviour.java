package com.uncreated.civilized.entity.behaviour.worker;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.mojang.datafixers.util.Pair;
import com.uncreated.civilized.entity.CivilizedVillager;
import com.uncreated.civilized.neoforge.registration.ai.AIRegistry;

import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.behavior.GateBehavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;

public class CoreWorkBehaviour extends MGateBehavior<CivilizedVillager> {
   public CoreWorkBehaviour(
         List<Pair<? extends BehaviorControl<? super CivilizedVillager>, Integer>> behavioursWithDurations) {
      super(
            Map.of(
                  MemoryModuleType.JOB_SITE,
                  MemoryStatus.VALUE_PRESENT,
                  AIRegistry.MM_DIALOGUE_TARGET.get(),
                  MemoryStatus.VALUE_ABSENT),
            Set.of(MemoryModuleType.WALK_TARGET, MemoryModuleType.LOOK_TARGET),
            GateBehavior.OrderPolicy.ORDERED,
            GateBehavior.RunningPolicy.TRY_ALL,
            behavioursWithDurations);
   }

}
