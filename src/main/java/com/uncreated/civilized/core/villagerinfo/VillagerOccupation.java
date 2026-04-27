package com.uncreated.civilized.core.villagerinfo;

import net.minecraft.network.chat.Component;

public enum VillagerOccupation {
   UNEMPLOYED,
   // resource gatherer
   FARMER,
   RANCHER,
   WOODCUTTER,
   STONECUTTER,
   MINER,
   BEEKEEPER,
   FISHERMAN,
   // artisan
   BAKER,
   BUTCHER,
   BLACKSMITH,
   TOOLSMITH,
   WEAPONSMITH,
   ARMORER,
   FLETCHER,
   CARPENTER,
    MASON,
   LEATHERWORKER,
   WEAVER,
   CARTOGRAPHER,
   ARTIST,
   // special
   TAVERN_KEEPER,
   PRIEST,
   SOLDIER,
   NOBLEMAN;

   public Component translation() {
      return Component.translatable("villager.occupation." + name().toLowerCase());
   }

   public boolean isArtisan() {
      return switch (this) {
      case BAKER, BUTCHER, BLACKSMITH, TOOLSMITH, WEAPONSMITH, ARMORER, CARPENTER, MASON, LEATHERWORKER, WEAVER,
           FLETCHER, CARTOGRAPHER, ARTIST ->
         true;
      default -> false;
      };
   }
}
