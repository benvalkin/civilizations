package com.uncreated.civilized.core.settlement;

import java.util.UUID;

import com.uncreated.civilized.core.InMemoryDB;
import com.uncreated.civilized.core.SetIndex;
import com.uncreated.civilized.core.building.Building;

import lombok.Getter;

public class SettlementDB extends InMemoryDB<UUID, Settlement> {

   @Override
   protected UUID getKey(Settlement obj) {
      return obj.getSettlementId();
   }
}
