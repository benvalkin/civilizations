package com.uncreated.civilized.core.settlement;

import lombok.Getter;
import net.minecraft.network.chat.Component;

public enum SettlementLevel {

   OUTPOST(1), HAMLET(2), VILLAGE(3), TOWN(4), CITY(5);

   @Getter
   private final int level;

   SettlementLevel(int level) {
      this.level = level;
   }

   public static SettlementLevel valueOf(int level) {
      for (SettlementLevel e : values()) {
         if (e.getLevel() == level) {
            return e;
         }
      }
      throw new IllegalArgumentException();
   }

   public Component translation() {
      return Component.translatable("settlement_level." + this.toString().toLowerCase());
   }
}
