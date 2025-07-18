package com.uncreated.civilized.core.villagerinfo;

import net.minecraft.network.chat.Component;

public enum VillagerRole {
   VILLAGER, TRAVELLER, BANDIT, ADVISOR;

   public Component translation() {
      return Component.translatable("villager.occupation." + name().toLowerCase());
   }
}
