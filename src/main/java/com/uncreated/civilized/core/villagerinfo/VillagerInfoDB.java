package com.uncreated.civilized.core.villagerinfo;

import java.util.UUID;

import com.uncreated.civilized.core.InMemoryDB;
import com.uncreated.civilized.core.SetIndex;

import lombok.Getter;

public class VillagerInfoDB extends InMemoryDB<UUID, VillagerInfo> {

   @Getter
   private final SetIndex<UUID, VillagerInfo> settlementsToVillagersIndex = new SetIndex<>();

   @Override
   protected UUID getKey(VillagerInfo obj) {
      return obj.getVillagerId();
   }

   @Override
   protected void index(VillagerInfo obj) {
      settlementsToVillagersIndex.add(obj.getSettlementId(), obj);
   }

   @Override
   protected void unindex(VillagerInfo obj) {
      settlementsToVillagersIndex.remove(obj.getSettlementId(), obj);
   }
}
