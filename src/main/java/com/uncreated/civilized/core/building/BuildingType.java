package com.uncreated.civilized.core.building;

import com.uncreated.civilized.core.villagerinfo.VillagerOccupation;
import com.uncreated.civilized.ui.style.Colors;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.util.Collection;
import java.util.List;

public enum BuildingType {
   NONE,
   INN,
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

   public VillagerOccupation getOccupation() {
      return switch (this) {
      case FARMER_HOUSE, CROP_FARM -> VillagerOccupation.FARMER;
      case WOODCUTTER_HOUSE, GROVE -> VillagerOccupation.WOODCUTTER;
      case MINER_HOUSE, MINE -> VillagerOccupation.MINER;
         case RANCHER_HOUSE, CATTLE_FARM, CHICKEN_FARM, SHEEP_FARM, HOG_FARM -> VillagerOccupation.RANCHER;
      default -> VillagerOccupation.UNEMPLOYED;
      };
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
      case GROVE, CROP_FARM, CATTLE_FARM, HOG_FARM, SHEEP_FARM, CHICKEN_FARM, QUARRY, MINE -> true;
      default -> false;
      };
   }

   public Collection<Item> getAnimalFoodItems() {
      return switch (this) {
         case CATTLE_FARM, SHEEP_FARM -> List.of(Items.WHEAT);
         case CHICKEN_FARM -> List.of(Items.WHEAT_SEEDS, Items.PUMPKIN_SEEDS, Items.MELON_SEEDS, Items.BEETROOT_SEEDS);
          case HOG_FARM -> List.of(Items.CARROT, Items.POTATO, Items.BEETROOT);
         default -> List.of();
      };
   }
}
