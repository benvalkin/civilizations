package com.uncreated.civilized.core.villagerinfo;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;

import com.google.common.collect.ImmutableList;
import com.mojang.logging.LogUtils;

import net.minecraft.world.level.saveddata.SavedData;

public abstract class VillagerStore extends SavedData {

   protected static final Logger LOGGER = LogUtils.getLogger();

   protected Map<UUID, VillagerInfo> villagers;

   protected VillagerStore() {
      villagers = new HashMap<>();
   }

   public ImmutableList<VillagerInfo> all() {
      return ImmutableList.copyOf(villagers.values());
   }

   public Optional<VillagerInfo> find(UUID villagerId) {
      return Optional.ofNullable(villagers.get(villagerId));
   }

   public VillagerInfo get(UUID villagerId) {
      return find(villagerId).orElseThrow();
   }
}
