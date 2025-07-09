package com.uncreated.civilized.core.settlement.events;

import com.uncreated.civilized.core.settlement.Settlement;

import lombok.Getter;
import net.neoforged.bus.api.Event;

@Getter
public class SettlementUpdatedEvent extends Event {

   public SettlementUpdatedEvent(Settlement settlement, boolean isClientside) {
      this.settlement = settlement;
      this.isClientside = isClientside;
   }

   private final Settlement settlement;
   private final boolean isClientside;
}
