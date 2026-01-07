package com.uncreated.civilized.core.settlement.entity;

import com.uncreated.civilized.core.building.entity.behaviour.BuildingBehaviour;
import com.uncreated.civilized.core.settlement.Settlement;

import com.uncreated.civilized.core.settlement.entity.behaviour.SettlementBehaviour;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.world.level.Level;

@Getter
public class LoadedSettlement {
   private final Settlement settlement;
   private final Level level;
   private final SettlementBehaviour behaviour;
   private int loadedBuildingsCount;

   void incrementLoadedBuildingsCount() {
      loadedBuildingsCount++;
   }
   void decrementLoadedBuildingsCount() {
      loadedBuildingsCount--;
   }

   public LoadedSettlement(Settlement settlement, Level level) {
      this.settlement = settlement;
      this.level = level;
      behaviour = new SettlementBehaviour(this);
   }
}
