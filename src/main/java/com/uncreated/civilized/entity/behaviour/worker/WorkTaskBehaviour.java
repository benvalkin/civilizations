package com.uncreated.civilized.entity.behaviour.worker;

import com.uncreated.civilized.entity.CivilizedVillager;
import com.uncreated.civilized.entity.behaviour.BehaviourState;

import com.uncreated.civilized.entity.behaviour.StatefulBehaviour;
import net.minecraft.server.level.ServerLevel;

public abstract class WorkTaskBehaviour extends StatefulBehaviour {

   public WorkTaskBehaviour(BehaviourState state, int duration, int cooldownDuration) {
      super(state, duration, cooldownDuration);
   }

   @Override
   protected void start(ServerLevel level, CivilizedVillager villager, long gameTime) {
      super.start(level, villager, gameTime);
   }

   @Override
   protected void stop(ServerLevel level, CivilizedVillager villager, long gameTime) {
      super.stop(level, villager, gameTime);
   }
}
