package com.uncreated.civilized.core.building.state;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;
import com.uncreated.civilized.core.building.Building;
import com.uncreated.civilized.core.building.state.artisan.ArmorerHouseState;
import com.uncreated.civilized.core.building.state.artisan.ArtistHouseState;
import com.uncreated.civilized.core.building.state.artisan.BakeryState;
import com.uncreated.civilized.core.building.state.artisan.BlacksmithHouseState;
import com.uncreated.civilized.core.building.state.artisan.ButcheryState;
import com.uncreated.civilized.core.building.state.artisan.CarpenterHouseState;
import com.uncreated.civilized.core.building.state.artisan.CartographerHouseState;
import com.uncreated.civilized.core.building.state.artisan.FletcherHouseState;
import com.uncreated.civilized.core.building.state.artisan.MasonHouseState;
import com.uncreated.civilized.core.building.state.artisan.LeatherworkerHouseState;
import com.uncreated.civilized.core.building.state.artisan.ToolsmithHouseState;
import com.uncreated.civilized.core.building.state.artisan.WeaponsmithHouseState;
import com.uncreated.civilized.core.building.state.artisan.WeaverHouseState;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;

public class BuildingState {

   protected final Logger LOGGER = LogUtils.getLogger();
   protected final Building building;

   protected BuildingState(Building building) {
      this.building = building;
   }

   public static BuildingState create(Building building) {
      return switch (building.getBuildingType()) {
      case CROP_FARM -> new CropFarmState(building);
      case GROVE -> new GroveState(building);
      case CATTLE_FARM, SHEEP_FARM, PIG_FARM, CHICKEN_FARM, BEE_FARM -> new AnimalFarmState(building);
      case BAKER_HOUSE -> new BakeryState(building);
      case ARMORER_HOUSE -> new ArmorerHouseState(building);
      case ARTIST_HOUSE -> new ArtistHouseState(building);
      case BLACKSMITH_HOUSE -> new BlacksmithHouseState(building);
      case BUTCHER_HOUSE -> new ButcheryState(building);
      case CARPENTER_HOUSE -> new CarpenterHouseState(building);
      case CARTOGRAPHER_HOUSE -> new CartographerHouseState(building);
      case FLETCHER_HOUSE -> new FletcherHouseState(building);
      case MASON_HOUSE -> new MasonHouseState(building);
      case LEATHERWORKER_HOUSE -> new LeatherworkerHouseState(building);
      case TOOLSMITH_HOUSE -> new ToolsmithHouseState(building);
      case WEAPONSMITH_HOUSE -> new WeaponsmithHouseState(building);
      case WEAVER_HOUSE -> new WeaverHouseState(building);
      default -> new BuildingState(building) {
      };
      };
   }

   public void applyNbt(CompoundTag compoundTag, HolderLookup.Provider registryAccess) {

   }

   public CompoundTag toNbt(HolderLookup.Provider registryAccess) {
      return new CompoundTag();
   }

   public void serverAddToBuildingScreenContext(CompoundTag compoundTag, ServerLevel serverLevel) {

   }
}
