package com.uncreated.civilized.entity.behaviour.worker;

import com.google.common.collect.ImmutableList;
import com.uncreated.civilized.entity.CivilizedVillager;
import com.uncreated.civilized.entity.behaviour.BehaviourState;
import com.uncreated.civilized.entity.behaviour.StatefulBehaviour;
import com.uncreated.civilized.entity.behaviour.StatefulBehaviourControl;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.schedule.Activity;

public class WorkBehaviourControl extends StatefulBehaviourControl<WorkStateMachine> {
   public WorkBehaviourControl(
         ImmutableList<StatefulBehaviour> tasks,
         ImmutableList<BehaviourState> coreTasks,
         ImmutableList<BehaviourState> idleTasks) {
      super(tasks, coreTasks, idleTasks);
   }

   @Override
   public WorkStateMachine createStateMachine() {
      return new WorkStateMachine();
   }

   @Override
   protected boolean canContinueToUse(ServerLevel serverLevel, CivilizedVillager civilizedVillager, long currentTicks) {
      return civilizedVillager.getBrain().isActive(Activity.WORK);
   }
}
