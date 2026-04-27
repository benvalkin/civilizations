package com.uncreated.civilized.core.building;

import com.uncreated.civilized.core.villagerinfo.VillagerOccupation;
import com.uncreated.civilized.ui.style.Colors;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public enum BuildingType {
   NONE,
   INN,
   TOWN_HALL,
   TOWN_SQUARE,
   STOREHOUSE,
   CHURCH,
   TAVERN,
   FARMER_HOUSE,
   RANCHER_HOUSE,
   WOODCUTTER_HOUSE,
   STONECUTTER_HOUSE,
   MINER_HOUSE,
   FISHERMAN_HOUSE,
   BEEKEEPER_HOUSE,
   BAKER_HOUSE,
   BUTCHER_HOUSE,
   BLACKSMITH_HOUSE,
   TOOLSMITH_HOUSE,
   WEAPONSMITH_HOUSE,
   ARMORER_HOUSE,
   CARPENTER_HOUSE,
   MASON_HOUSE,
   LEATHERWORKER_HOUSE,
   WEAVER_HOUSE,
   FLETCHER_HOUSE,
   CARTOGRAPHER_HOUSE,
   ARTIST_HOUSE,
   GROVE,
   CROP_FARM,
   CATTLE_FARM,
   PIG_FARM,
   SHEEP_FARM,
   CHICKEN_FARM,
   QUARRY,
   MINE,
   BEE_FARM,
   FISHING_SPOT,
   BARRACKS,
   GUARD_POST,
   FANCY_HOUSE;

   public String translationKey() {
      return "building." + this.name().toLowerCase();
   }

   public MutableComponent translation() {
      return Component.translatableWithFallback(translationKey(), this.name().toLowerCase().replace("_", " "))
            .withColor(Colors.BUILDING_LIGHT);
   }

   public MutableComponent translationDark() {
      return Component.translatableWithFallback(translationKey(), this.name().toLowerCase().replace("_", " "))
            .withColor(Colors.BUILDING_DARK);
   }

   public VillagerOccupation getOccupation() {
      return switch (this) {
      case FARMER_HOUSE, CROP_FARM -> VillagerOccupation.FARMER;
      case WOODCUTTER_HOUSE, GROVE -> VillagerOccupation.WOODCUTTER;
      case STONECUTTER_HOUSE, QUARRY -> VillagerOccupation.STONECUTTER;
      case MINER_HOUSE, MINE -> VillagerOccupation.MINER;
      case RANCHER_HOUSE, CATTLE_FARM, CHICKEN_FARM, SHEEP_FARM, PIG_FARM -> VillagerOccupation.RANCHER;
      case BEEKEEPER_HOUSE -> VillagerOccupation.BEEKEEPER;
      case FISHERMAN_HOUSE -> VillagerOccupation.FISHERMAN;
      case BAKER_HOUSE -> VillagerOccupation.BAKER;
      case BUTCHER_HOUSE -> VillagerOccupation.BUTCHER;
      case BLACKSMITH_HOUSE -> VillagerOccupation.BLACKSMITH;
      case TOOLSMITH_HOUSE -> VillagerOccupation.TOOLSMITH;
      case WEAPONSMITH_HOUSE -> VillagerOccupation.WEAPONSMITH;
      case ARMORER_HOUSE -> VillagerOccupation.ARMORER;
      case CARPENTER_HOUSE -> VillagerOccupation.CARPENTER;
      case MASON_HOUSE -> VillagerOccupation.MASON;
      case LEATHERWORKER_HOUSE -> VillagerOccupation.LEATHERWORKER;
      case WEAVER_HOUSE -> VillagerOccupation.WEAVER;
      case CARTOGRAPHER_HOUSE -> VillagerOccupation.CARTOGRAPHER;
      case ARTIST_HOUSE -> VillagerOccupation.ARTIST;
      case CHURCH -> VillagerOccupation.PRIEST;
      case BARRACKS, GUARD_POST -> VillagerOccupation.SOLDIER;
      case TAVERN -> VillagerOccupation.TAVERN_KEEPER;
      default -> VillagerOccupation.UNEMPLOYED;
      };
   }

   public boolean isPermanentResidence() {

      if (isWorksite())
         return false;

      return switch (this) {
      case INN, STOREHOUSE, TOWN_SQUARE -> false;
      default -> true;
      };
   }

   public boolean isArtisanBuilding() {
      return switch (this) {
      case BAKER_HOUSE, BUTCHER_HOUSE, LEATHERWORKER_HOUSE, WEAVER_HOUSE, BLACKSMITH_HOUSE, TOOLSMITH_HOUSE, WEAPONSMITH_HOUSE,
           ARMORER_HOUSE, CARPENTER_HOUSE, MASON_HOUSE, ARTIST_HOUSE, CARTOGRAPHER_HOUSE ->
         true;
      default -> false;
      };
   }

   public boolean isTemporaryResidence() {
      return this == INN;
   }

   public boolean isResidence() {
      return isPermanentResidence() || isTemporaryResidence();
   }

   public boolean canHaveOccupants() {
      return isResidence() || isWorksite();
   }

   public boolean isWorksite() {
      return switch (this) {
      case GROVE, CROP_FARM, CATTLE_FARM, PIG_FARM, SHEEP_FARM, CHICKEN_FARM, QUARRY, MINE, BEE_FARM, FISHING_SPOT ->
         true;
      default -> false;
      };
   }

   public boolean isAnimalFarm() {
      return switch (this) {
      case CATTLE_FARM, PIG_FARM, SHEEP_FARM, CHICKEN_FARM, BEE_FARM -> true;
      default -> false;
      };
   }
}
