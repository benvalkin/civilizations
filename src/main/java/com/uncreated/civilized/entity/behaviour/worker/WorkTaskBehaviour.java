package com.uncreated.civilized.entity.behaviour.worker;

import java.util.Map;

import com.uncreated.civilized.entity.CivilizedVillager;
import com.uncreated.civilized.neoforge.registration.ai.AIRegistry;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;

public class WorkTaskBehaviour extends Behavior<CivilizedVillager> {

   public WorkTaskBehaviour(Map<MemoryModuleType<?>, MemoryStatus> entryCondition, int minDuration, int maxDuration) {
      super(entryCondition, minDuration, maxDuration);
   }

   public WorkTaskBehaviour(Map<MemoryModuleType<?>, MemoryStatus> entryCondition){
        this(entryCondition, 20 * 60 * 2,  20 * 60 * 2);
    }

   @Override
   protected void start(ServerLevel level, CivilizedVillager villager, long gameTime) {
      villager.getBrain().setMemory(AIRegistry.MM_HAS_NON_IDLE_WORK_TASK.get(), true);
   }

   @Override
   protected void stop(ServerLevel level, CivilizedVillager villager, long gameTime) {
      villager.getBrain().eraseMemory(AIRegistry.MM_HAS_NON_IDLE_WORK_TASK.get());
   }
}
