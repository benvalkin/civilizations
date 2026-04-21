package com.uncreated.civilized.entity.behaviour;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
public class BehaviourState {

   private final String value;

   public BehaviourState(String value) {
      this.value = value;
   }

   public boolean is(BehaviourState other) {
      return this.value.equals(other.value);
   }

   @Override
   public String toString() {
      return value;
   }
}
