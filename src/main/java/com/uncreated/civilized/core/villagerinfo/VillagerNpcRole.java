package com.uncreated.civilized.core.villagerinfo;

import net.minecraft.network.chat.Component;

public enum VillagerNpcRole {
   WORKER, SPOUSE, TRAVELLER, MIGRANT, BANDIT, ADVISOR;

   public Component translation() {
      return Component.translatable("villager.occupation." + name().toLowerCase());
   }
}
