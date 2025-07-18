package com.uncreated.civilized.core.building;

import com.uncreated.civilized.core.villagerinfo.VillagerOccupation;
import com.uncreated.civilized.ui.style.Colors;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public enum BuildingType {
   NONE,
   TRADING_POST,
   TOWN_HALL,
   STOREHOUSE,
   CHURCH,
   TAVERN,
   FARMER_HOUSE,
   RANCHER_HOUSE,
   WOODCUTTER_HOUSE,
   MINER_HOUSE,
   BEEKEEPER_HOUSE,
   BAKER_HOUSE,
   BUTCHER_HOUSE,
   TANNER_HOUSE,
   GROVE,
   CROP_FARM,
   CATTLE_FARM,
   HOG_FARM,
   SHEEP_FARM,
   CHICKEN_FARM,
   QUARRY,
   MINE,
   BARRACKS,
   GUARD_POST;

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

   public VillagerOccupation toJobType() {
      switch (this) {
      case FARMER_HOUSE:
         return VillagerOccupation.FARMER;
      default:
         return VillagerOccupation.UNEMPLOYED;
      }
   }

   public boolean isPermanentResidence() {
      return switch (this) {
      case FARMER_HOUSE, RANCHER_HOUSE, WOODCUTTER_HOUSE, MINER_HOUSE, BEEKEEPER_HOUSE, BAKER_HOUSE, BUTCHER_HOUSE,
            TANNER_HOUSE, BARRACKS, GUARD_POST ->
         true;
      default -> false;
      };
   }

   public boolean isTemporaryResidence() {
      return this == TRADING_POST;
   }

   public boolean isResidence() {
      return switch (this) {
         case TRADING_POST -> true;
         default -> false;
      };
   }

   public boolean canHaveOccupants() {
       return isResidence() || isWorksite();
   }

   public boolean isWorksite() {
      return switch (this) {
      case GROVE, CROP_FARM, CATTLE_FARM, HOG_FARM, SHEEP_FARM, CHICKEN_FARM, QUARRY, MINE -> true;
      default -> false;
      };
   }
}
