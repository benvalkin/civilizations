package com.uncreated.civilized.core.villagerinfo.events.model;

import com.uncreated.civilized.core.villagerinfo.VillagerInfo;

import lombok.Getter;
import net.neoforged.bus.api.Event;

@Getter
public class VillagerInfoUpdatedEvent extends Event {

   public VillagerInfoUpdatedEvent(VillagerInfo villagerInfo, boolean isClientside) {
      this.villagerInfo = villagerInfo;
      this.isClientside = isClientside;
   }

   private final VillagerInfo villagerInfo;
   private final boolean isClientside;
}
