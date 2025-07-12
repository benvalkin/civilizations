package com.uncreated.civilized.core.settlement;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;

import com.google.common.collect.ImmutableList;
import com.mojang.logging.LogUtils;

import lombok.Getter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;

public abstract class SettlementsStore extends SavedData {

   protected static final Logger LOGGER = LogUtils.getLogger();

   public abstract Level getLevel();

   protected Map<UUID, Settlement> settlements;

   protected SettlementsStore() {
      settlements = new HashMap<>();
   }

   public ImmutableList<Settlement> all() {
      return ImmutableList.copyOf(settlements.values());
   }

   public Settlement createNew(UUID ownerUUID) {
      Settlement settlement =
            new Settlement.SettlementBuilder().settlementId(UUID.randomUUID())
                  .ownerId(ownerUUID)
                  .displayName(Settlement.generateRandomName())
                  .build();

      settlements.put(settlement.getSettlementId(), settlement);
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
      return settlements.values().stream().filter(s -> s.getOwnerId().equals(ownerId)).findFirst();
   }

   public Settlement getFromOwner(UUID ownerId) {
      return findFromOwner(ownerId).orElseThrow();
   }
}
