package com.uncreated.civilized.core.settlement;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;

import com.google.common.collect.ImmutableList;
import com.mojang.logging.LogUtils;
import com.uncreated.civilized.core.building.Building;
import com.uncreated.civilized.core.villagerinfo.VillagerInfo;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;

public abstract class SettlementsStore extends SavedData {

   protected static final Logger LOGGER = LogUtils.getLogger();

   public abstract Level getLevel();

   protected SettlementDB settlements;

   protected SettlementsStore() {
      settlements = new SettlementDB();
   }

   public ImmutableList<Settlement> all() {
      return ImmutableList.copyOf(settlements.all());
   }

   public Settlement createNew(UUID ownerUUID) {
      Settlement settlement =
            new Settlement.SettlementBuilder().settlementId(UUID.randomUUID())
                  .ownerId(ownerUUID)
                  .displayName(Settlement.generateRandomName())
                  .build();

      settlements.add(settlement);
      setDirty();
      return settlement;
   }

   public Optional<Settlement> find(UUID settlementId) {
      return Optional.ofNullable(settlements.get(settlementId));
   }

   public Settlement get(UUID settlementId) {
      return find(settlementId).orElseThrow();
   }

   public Optional<Settlement> findFromOwner(UUID ownerId) {
      return settlements.all().stream().filter(s -> s.getOwnerId().equals(ownerId)).findFirst();
   }

   public Settlement getFromOwner(UUID ownerId) {
      return findFromOwner(ownerId).orElseThrow();
   }
}
