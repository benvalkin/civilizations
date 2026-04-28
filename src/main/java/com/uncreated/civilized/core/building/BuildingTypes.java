package com.uncreated.civilized.core.building;

import java.util.List;

import com.uncreated.civilized.core.building.entity.behaviour.ArtisanHouseBehaviour;
import com.uncreated.civilized.core.building.production.bills.ProductionType;
import com.uncreated.civilized.core.building.state.AnimalFarmState;
import com.uncreated.civilized.core.building.state.CropFarmState;
import com.uncreated.civilized.core.building.state.GroveState;
import com.uncreated.civilized.core.building.state.artisan.ArmorerHouseState;
import com.uncreated.civilized.core.building.state.artisan.ArtistHouseState;
import com.uncreated.civilized.core.building.state.artisan.BakeryState;
import com.uncreated.civilized.core.building.state.artisan.BlacksmithHouseState;
import com.uncreated.civilized.core.building.state.artisan.ButcheryState;
import com.uncreated.civilized.core.building.state.artisan.CarpenterHouseState;
import com.uncreated.civilized.core.building.state.artisan.CartographerHouseState;
import com.uncreated.civilized.core.building.state.artisan.FletcherHouseState;
import com.uncreated.civilized.core.building.state.artisan.LeatherworkerHouseState;
import com.uncreated.civilized.core.building.state.artisan.MasonHouseState;
import com.uncreated.civilized.core.building.state.artisan.ToolsmithHouseState;
import com.uncreated.civilized.core.building.state.artisan.WeaponsmithHouseState;
import com.uncreated.civilized.core.building.state.artisan.WeaverHouseState;

public class BuildingTypes {

   public static BuildingType TOWN_HALL = BuildingType.builder("town_hall").build();
   public static BuildingType INN = BuildingType.builder("inn").isTemporaryResidence(true).build();
   public static BuildingType TOWN_SQUARE = BuildingType.builder("town_square").build();
   public static BuildingType STOREHOUSE = BuildingType.builder("storehouse").build();
   public static BuildingType CHURCH = BuildingType.builder("church").isPermanentResidence(true).build();
   public static BuildingType BARRACKS = BuildingType.builder("barracks").isPermanentResidence(true).build();
   public static BuildingType GUARD_POST = BuildingType.builder("guard_post").isPermanentResidence(true).build();
   public static BuildingType TAVERN = BuildingType.builder("tavern").isPermanentResidence(true).build();
   public static BuildingType FARMER_HOUSE = BuildingType.builder("farmer_house").isPermanentResidence(true).build();
   public static BuildingType MINER_HOUSE = BuildingType.builder("miner_house").isPermanentResidence(true).build();

   public static BuildingType STONECUTTER_HOUSE =
         BuildingType.builder("stonecutter_house").isPermanentResidence(true).build();

   public static BuildingType RANCHER_HOUSE = BuildingType.builder("rancher_house").isPermanentResidence(true).build();

   public static BuildingType FISHERMAN_HOUSE =
         BuildingType.builder("fisherman_house").isPermanentResidence(true).build();
   public static BuildingType BEEKEEPER_HOUSE = BuildingType.builder("farmer_house").isPermanentResidence(true).build();

   public static BuildingType BAKER_HOUSE =
         BuildingType.builder("baker_house")
               .isPermanentResidence(true)
               .supportedProductionTypes(List.of(ProductionType.CRAFTING, ProductionType.SMELTING))
               .createState(BakeryState::new)
               .createBehaviour(ArtisanHouseBehaviour::new)
               .build();

   public static BuildingType BUTCHER_HOUSE =
         BuildingType.builder("butcher_house")
               .isPermanentResidence(true)
               .supportedProductionTypes(List.of(ProductionType.CRAFTING, ProductionType.SMOKING))
               .createState(ButcheryState::new)

               .createBehaviour(ArtisanHouseBehaviour::new)
               .build();

   public static BuildingType BLACKSMITH_HOUSE =
         BuildingType.builder("blacksmith_house")
               .isPermanentResidence(true)
               .supportedProductionTypes(List.of(ProductionType.CRAFTING, ProductionType.BLASTING))
               .createState(BlacksmithHouseState::new)

               .createBehaviour(ArtisanHouseBehaviour::new)
               .build();

   public static BuildingType MASON_HOUSE =
         BuildingType.builder("mason_house")
               .isPermanentResidence(true)
               .supportedProductionTypes(List.of(ProductionType.CRAFTING, ProductionType.SMELTING))
               .createState(MasonHouseState::new)
               .createBehaviour(ArtisanHouseBehaviour::new)
               .build();

   public static BuildingType CARPENTER_HOUSE =
         BuildingType.builder("carpenter_house")
               .isPermanentResidence(true)
               .supportedProductionTypes(List.of(ProductionType.CRAFTING))
               .createState(CarpenterHouseState::new)
               .createBehaviour(ArtisanHouseBehaviour::new)
               .build();

   public static BuildingType TOOLSMITH_HOUSE =
         BuildingType.builder("toolsmith_house")
               .isPermanentResidence(true)
               .supportedProductionTypes(List.of(ProductionType.CRAFTING))
               .createState(ToolsmithHouseState::new)
               .createBehaviour(ArtisanHouseBehaviour::new)
               .build();

   public static BuildingType WEAPONSMITH_HOUSE =
         BuildingType.builder("weaponsmith_house")
               .isPermanentResidence(true)
               .supportedProductionTypes(List.of(ProductionType.CRAFTING))
               .createState(WeaponsmithHouseState::new)
               .createBehaviour(ArtisanHouseBehaviour::new)
               .build();

   public static BuildingType ARMORER_HOUSE =
         BuildingType.builder("armorer_house")
               .isPermanentResidence(true)
               .supportedProductionTypes(List.of(ProductionType.CRAFTING))
               .createState(ArmorerHouseState::new)
               .createBehaviour(ArtisanHouseBehaviour::new)
               .build();

   public static BuildingType LEATHERWORKER_HOUSE =
         BuildingType.builder("leatherworker_house")
               .isPermanentResidence(true)
               .supportedProductionTypes(List.of(ProductionType.CRAFTING))
               .createState(LeatherworkerHouseState::new)
               .createBehaviour(ArtisanHouseBehaviour::new)
               .build();
   public static BuildingType WEAVER_HOUSE =
         BuildingType.builder("weaver_house")
               .isPermanentResidence(true)
               .supportedProductionTypes(List.of(ProductionType.CRAFTING))
               .createState(WeaverHouseState::new)
               .createBehaviour(ArtisanHouseBehaviour::new)
               .build();

   public static BuildingType FLETCHER_HOUSE =
         BuildingType.builder("fletcher_house")
               .isPermanentResidence(true)
               .supportedProductionTypes(List.of(ProductionType.CRAFTING))
               .createState(FletcherHouseState::new)
               .createBehaviour(ArtisanHouseBehaviour::new)
               .build();

   public static BuildingType CARTOGRAPHER_HOUSE =
         BuildingType.builder("cartographer_house")
               .isPermanentResidence(true)
               .supportedProductionTypes(List.of(ProductionType.CRAFTING))
               .createState(CartographerHouseState::new)
               .createBehaviour(ArtisanHouseBehaviour::new)
               .build();

   public static BuildingType ARTIST_HOUSE =
         BuildingType.builder("artist_house")
               .isPermanentResidence(true)
               .supportedProductionTypes(List.of(ProductionType.CRAFTING))
               .createState(ArtistHouseState::new)
               .createBehaviour(ArtisanHouseBehaviour::new)
               .build();

   public static BuildingType CROP_FARM =
         BuildingType.builder("crop_farm").isWorksite(true).createState(CropFarmState::new).build();

   public static BuildingType COW_FARM =
         BuildingType.builder("cow_farm").isWorksite(true).isAnimalFarm(true).createState(AnimalFarmState::new).build();

   public static BuildingType SHEEP_FARM =
         BuildingType.builder("sheep_farm").isWorksite(true).isAnimalFarm(true).build();

   public static BuildingType PIG_FARM =
         BuildingType.builder("pig_farm").isWorksite(true).isAnimalFarm(true).createState(AnimalFarmState::new).build();

   public static BuildingType CHICKEN_FARM =
         BuildingType.builder("chicken_farm").isWorksite(true).isAnimalFarm(true).build();

   public static BuildingType BEE_FARM =
         BuildingType.builder("bee_farm").isWorksite(true).isAnimalFarm(true).createState(AnimalFarmState::new).build();
   public static BuildingType MINE = BuildingType.builder("mine").isWorksite(true).build();
   public static BuildingType QUARRY = BuildingType.builder("quarry").isWorksite(true).build();
   public static BuildingType GROVE =
         BuildingType.builder("grove").isWorksite(true).createState(GroveState::new).build();

}
