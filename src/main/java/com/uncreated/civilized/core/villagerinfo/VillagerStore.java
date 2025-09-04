package com.uncreated.civilized.core.villagerinfo;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;

import com.google.common.collect.ImmutableList;
import com.mojang.logging.LogUtils;

import net.minecraft.world.level.saveddata.SavedData;

public abstract class VillagerStore extends SavedData {

   protected static final Logger LOGGER = LogUtils.getLogger();

   protected VillagerInfoDB villagers;

   protected VillagerStore() {
      villagers = new VillagerInfoDB();
   }

   public ImmutableList<VillagerInfo> all() {
      return villagers.all();
   }

   public Optional<VillagerInfo> find(UUID villagerId) {
      return villagers.find(villagerId);
   }

   public Set<VillagerInfo> getCitizens(UUID settlementId) {
      return villagers.getSettlementsToVillagersIndex().getValues(settlementId);
   }

   public VillagerInfo get(UUID villagerId) {
      return villagers.get(villagerId);
   }
}
