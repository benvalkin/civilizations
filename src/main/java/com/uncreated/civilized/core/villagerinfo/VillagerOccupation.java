package com.uncreated.civilized.core.villagerinfo;

import net.minecraft.network.chat.Component;

public enum VillagerOccupation {
   UNEMPLOYED, FARMER, RANCHER, WOODCUTTER, MINER, BAKER, BUTCHER, BEEKEEPER, TANNER, PRIEST, SOLDIER;

   public Component translation() {
      return Component.translatable("villager.occupation." + name().toLowerCase());
   }
}
