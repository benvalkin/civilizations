package com.uncreated.civilized.entity.behaviour;

import java.util.LinkedList;
import java.util.Optional;
import java.util.Queue;

import org.jetbrains.annotations.NotNull;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Accessors(fluent = true)
public class BehaviourStateMachine {

   private Queue<BehaviourState> queue;

   @Getter
   @Setter(AccessLevel.PACKAGE)
   private BehaviourState currentState;

   @Getter
   @Setter(AccessLevel.PACKAGE)
   private boolean isIdle;

   public BehaviourStateMachine() {
      queue = new LinkedList<>();
      currentState = BehaviourStates.NONE;
   }

   public void queueAction(@NotNull BehaviourState newState) {
      queue.add(newState);
   }

   public void queueActionOnce(@NotNull BehaviourState newState) {
      if (queue.contains(newState))
         return;

      queue.add(newState);
   }

   Optional<BehaviourState> pollNextAction() {
      if (queue.isEmpty())
         return Optional.empty();

      return Optional.ofNullable(queue.poll());
   }

   boolean hasQueuedActions() {
      return !queue.isEmpty();
   }
}
