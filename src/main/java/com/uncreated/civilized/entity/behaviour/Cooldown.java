package com.uncreated.civilized.entity.behaviour;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
public class Cooldown {

   private final String key;

   public Cooldown(String key) {
      this.key = key;
   }

   public boolean is(Cooldown other) {
      return this.key.equals(other.key);
   }

   @Override
   public String toString() {
      return key;
   }
}
