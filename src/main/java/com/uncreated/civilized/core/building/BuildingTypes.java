package com.uncreated.civilized.core.building;

import static com.uncreated.civilized.CivilizedMod.CIVILIZED_MOD_ID;

import java.util.List;

import com.uncreated.civilized.core.building.entity.behaviour.ArtisanHouseBehaviour;
import com.uncreated.civilized.core.building.production.bills.ProductionType;
import com.uncreated.civilized.core.building.state.CropFarmState;
import com.uncreated.civilized.core.building.state.GroveState;
import com.uncreated.civilized.core.building.state.animalfarm.BeeFarmState;
import com.uncreated.civilized.core.building.state.animalfarm.ChickenFarmState;
import com.uncreated.civilized.core.building.state.animalfarm.CowFarmState;
import com.uncreated.civilized.core.building.state.animalfarm.PigFarmState;
import com.uncreated.civilized.core.building.state.animalfarm.SheepFarmState;
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
import com.uncreated.civilized.ui.menu.building.inn.InnBuildingScreen;
import com.uncreated.civilized.ui.menu.building.residence.artisan.BakeryBuildingScreen;
import com.uncreated.civilized.ui.menu.building.residence.artisan.BlacksmithBuildingScreen;
import com.uncreated.civilized.ui.menu.building.residence.artisan.ButcheryBuildingScreen;
import com.uncreated.civilized.ui.menu.building.residence.artisan.CraftsmanHouseBuildingScreen;
import com.uncreated.civilized.ui.menu.building.residence.artisan.MasonBuildingScreen;
import com.uncreated.civilized.ui.menu.building.worksite.WorksiteBuildingScreen;
import com.uncreated.civilized.ui.menu.building.worksite.animalfarm.AnimalFarmBuildingScreen;
import com.uncreated.civilized.ui.menu.building.worksite.cropfarm.CropFarmBuildingScreen;
import com.uncreated.civilized.ui.menu.building.worksite.grove.GroveBuildingScreen;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegisterEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;

public class BuildingTypes {

   private static final ResourceKey<Registry<BuildingType>> BUILDING_TYPES_KEY =
         ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(CIVILIZED_MOD_ID, "building_types"));
   private static final Registry<BuildingType> BUILDING_TYPES_INTERNAL =
         new RegistryBuilder<>(BUILDING_TYPES_KEY).create();

   public static final DeferredRegister<BuildingType> BUILDING_TYPES =
         DeferredRegister.create(BUILDING_TYPES_INTERNAL, CIVILIZED_MOD_ID);

   public static ResourceLocation createResourceKey(String buildingTypeName) {
      return ResourceLocation.fromNamespaceAndPath(CIVILIZED_MOD_ID, buildingTypeName);
   }

   public static BuildingType getFromResourceLocation(ResourceLocation resourceLocation) {
      return BuildingTypes.BUILDING_TYPES.getRegistry().get().getValue(resourceLocation);
   }

   @SubscribeEvent
   private static void registerRegistry(NewRegistryEvent event) {
      event.register(BUILDING_TYPES_INTERNAL);
   }

   @SubscribeEvent
   private static void registerTypes(RegisterEvent event) {
      event.register(BUILDING_TYPES_KEY, registry -> {
         registerBuildingType(registry, TOWN_HALL);
         registerBuildingType(registry, INN);
         registerBuildingType(registry, TOWN_SQUARE);
         registerBuildingType(registry, CHURCH);
         registerBuildingType(registry, BARRACKS);
         registerBuildingType(registry, GUARD_POST);
         registerBuildingType(registry, TAVERN);
         registerBuildingType(registry, FARMER_HOUSE);
         registerBuildingType(registry, WOODCUTTER_HOUSE);
         registerBuildingType(registry, MINER_HOUSE);
         registerBuildingType(registry, STONECUTTER_HOUSE);
         registerBuildingType(registry, RANCHER_HOUSE);
         registerBuildingType(registry, FISHERMAN_HOUSE);
         registerBuildingType(registry, BEEKEEPER_HOUSE);
         registerBuildingType(registry, BAKER_HOUSE);
         registerBuildingType(registry, BUTCHER_HOUSE);
         registerBuildingType(registry, BLACKSMITH_HOUSE);
         registerBuildingType(registry, MASON_HOUSE);
         registerBuildingType(registry, CARPENTER_HOUSE);
         registerBuildingType(registry, TOOLSMITH_HOUSE);
         registerBuildingType(registry, WEAPONSMITH_HOUSE);
         registerBuildingType(registry, ARMORER_HOUSE);
         registerBuildingType(registry, FLETCHER_HOUSE);
         registerBuildingType(registry, LEATHERWORKER_HOUSE);
         registerBuildingType(registry, WEAVER_HOUSE);
         registerBuildingType(registry, CARTOGRAPHER_HOUSE);
         registerBuildingType(registry, ARTIST_HOUSE);
         registerBuildingType(registry, CROP_FARM);
         registerBuildingType(registry, MINE);
         registerBuildingType(registry, GROVE);
         registerBuildingType(registry, QUARRY);
         registerBuildingType(registry, COW_FARM);
         registerBuildingType(registry, PIG_FARM);
         registerBuildingType(registry, SHEEP_FARM);
         registerBuildingType(registry, CHICKEN_FARM);
         registerBuildingType(registry, BEE_FARM);
         registerBuildingType(registry, FISHING_SPOT);
      });
   }

   private static void registerBuildingType(
         RegisterEvent.RegisterHelper<BuildingType> registry,
         BuildingType buildingType) {
      ResourceLocation resourceLocation =
            ResourceLocation.fromNamespaceAndPath(CIVILIZED_MOD_ID, buildingType.toString());
      registry.register(resourceLocation, buildingType);
   }

   public static BuildingType TOWN_HALL = BuildingType.builder(createResourceKey("town_hall")).build();
   public static BuildingType INN =
         BuildingType.builder(createResourceKey("inn"))
               .isResidence(true)
               .buildingScreenSupplier(InnBuildingScreen::new)
               .build();
   public static BuildingType TOWN_SQUARE = BuildingType.builder(createResourceKey("town_square")).build();
   public static BuildingType STOREHOUSE = BuildingType.builder(createResourceKey("storehouse")).build();
   public static BuildingType CHURCH = BuildingType.builder(createResourceKey("church")).isResidence(true).build();
   public static BuildingType BARRACKS = BuildingType.builder(createResourceKey("barracks")).isResidence(true).build();
   public static BuildingType GUARD_POST =
         BuildingType.builder(createResourceKey("guard_post")).isResidence(true).build();
   public static BuildingType TAVERN = BuildingType.builder(createResourceKey("tavern")).isResidence(true).build();
   public static BuildingType FARMER_HOUSE =
         BuildingType.builder(createResourceKey("farmer_house")).isResidence(true).build();
   public static BuildingType WOODCUTTER_HOUSE =
         BuildingType.builder(createResourceKey("woodcutter_house")).isResidence(true).build();
   public static BuildingType MINER_HOUSE =
         BuildingType.builder(createResourceKey("miner_house")).isResidence(true).build();

   public static BuildingType STONECUTTER_HOUSE =
         BuildingType.builder(createResourceKey("stonecutter_house")).isResidence(true).build();

   public static BuildingType RANCHER_HOUSE =
         BuildingType.builder(createResourceKey("rancher_house")).isResidence(true).build();

   public static BuildingType FISHERMAN_HOUSE =
         BuildingType.builder(createResourceKey("fisherman_house")).isResidence(true).build();
   public static BuildingType BEEKEEPER_HOUSE =
         BuildingType.builder(createResourceKey("farmer_house")).isResidence(true).build();

   public static BuildingType BAKER_HOUSE =
         BuildingType.builder(createResourceKey("baker_house"))
               .isResidence(true)
               .supportedProductionTypes(List.of(ProductionType.CRAFTING, ProductionType.SMELTING))
               .createState(BakeryState::new)
               .createBehaviour(ArtisanHouseBehaviour::new)
               .buildingScreenSupplier(BakeryBuildingScreen::new)
               .build();

   public static BuildingType BUTCHER_HOUSE =
         BuildingType.builder(createResourceKey("butcher_house"))
               .isResidence(true)
               .supportedProductionTypes(List.of(ProductionType.CRAFTING, ProductionType.SMOKING))
               .createState(ButcheryState::new)
               .createBehaviour(ArtisanHouseBehaviour::new)
               .buildingScreenSupplier(ButcheryBuildingScreen::new)
               .build();

   public static BuildingType BLACKSMITH_HOUSE =
         BuildingType.builder(createResourceKey("blacksmith_house"))
               .isResidence(true)
               .supportedProductionTypes(List.of(ProductionType.CRAFTING, ProductionType.BLASTING))
               .createState(BlacksmithHouseState::new)
               .createBehaviour(ArtisanHouseBehaviour::new)
               .buildingScreenSupplier(BlacksmithBuildingScreen::new)
               .build();

   public static BuildingType MASON_HOUSE =
         BuildingType.builder(createResourceKey("mason_house"))
               .isResidence(true)
               .supportedProductionTypes(List.of(ProductionType.CRAFTING, ProductionType.SMELTING))
               .createState(MasonHouseState::new)
               .createBehaviour(ArtisanHouseBehaviour::new)
               .buildingScreenSupplier(MasonBuildingScreen::new)
               .build();

   public static BuildingType CARPENTER_HOUSE =
         BuildingType.builder(createResourceKey("carpenter_house"))
               .isResidence(true)
               .supportedProductionTypes(List.of(ProductionType.CRAFTING))
               .createState(CarpenterHouseState::new)
               .createBehaviour(ArtisanHouseBehaviour::new)
               .buildingScreenSupplier(CraftsmanHouseBuildingScreen::new)
               .build();

   public static BuildingType TOOLSMITH_HOUSE =
         BuildingType.builder(createResourceKey("toolsmith_house"))
               .isResidence(true)
               .supportedProductionTypes(List.of(ProductionType.CRAFTING))
               .createState(ToolsmithHouseState::new)
               .createBehaviour(ArtisanHouseBehaviour::new)
               .buildingScreenSupplier(CraftsmanHouseBuildingScreen::new)
               .build();

   public static BuildingType WEAPONSMITH_HOUSE =
         BuildingType.builder(createResourceKey("weaponsmith_house"))
               .isResidence(true)
               .supportedProductionTypes(List.of(ProductionType.CRAFTING))
               .createState(WeaponsmithHouseState::new)
               .createBehaviour(ArtisanHouseBehaviour::new)
               .buildingScreenSupplier(CraftsmanHouseBuildingScreen::new)
               .build();

   public static BuildingType ARMORER_HOUSE =
         BuildingType.builder(createResourceKey("armorer_house"))
               .isResidence(true)
               .supportedProductionTypes(List.of(ProductionType.CRAFTING))
               .createState(ArmorerHouseState::new)
               .createBehaviour(ArtisanHouseBehaviour::new)
               .buildingScreenSupplier(CraftsmanHouseBuildingScreen::new)
               .build();

   public static BuildingType LEATHERWORKER_HOUSE =
         BuildingType.builder(createResourceKey("leatherworker_house"))
               .isResidence(true)
               .supportedProductionTypes(List.of(ProductionType.CRAFTING))
               .createState(LeatherworkerHouseState::new)
               .createBehaviour(ArtisanHouseBehaviour::new)
               .buildingScreenSupplier(CraftsmanHouseBuildingScreen::new)
               .build();
   public static BuildingType WEAVER_HOUSE =
         BuildingType.builder(createResourceKey("weaver_house"))
               .isResidence(true)
               .supportedProductionTypes(List.of(ProductionType.CRAFTING))
               .createState(WeaverHouseState::new)
               .createBehaviour(ArtisanHouseBehaviour::new)
               .buildingScreenSupplier(CraftsmanHouseBuildingScreen::new)
               .build();

   public static BuildingType FLETCHER_HOUSE =
         BuildingType.builder(createResourceKey("fletcher_house"))
               .isResidence(true)
               .supportedProductionTypes(List.of(ProductionType.CRAFTING))
               .createState(FletcherHouseState::new)
               .createBehaviour(ArtisanHouseBehaviour::new)
               .buildingScreenSupplier(CraftsmanHouseBuildingScreen::new)
               .build();

   public static BuildingType CARTOGRAPHER_HOUSE =
         BuildingType.builder(createResourceKey("cartographer_house"))
               .isResidence(true)
               .supportedProductionTypes(List.of(ProductionType.CRAFTING))
               .createState(CartographerHouseState::new)
               .createBehaviour(ArtisanHouseBehaviour::new)
               .buildingScreenSupplier(CraftsmanHouseBuildingScreen::new)
               .build();

   public static BuildingType ARTIST_HOUSE =
         BuildingType.builder(createResourceKey("artist_house"))
               .isResidence(true)
               .supportedProductionTypes(List.of(ProductionType.CRAFTING))
               .createState(ArtistHouseState::new)
               .createBehaviour(ArtisanHouseBehaviour::new)
               .buildingScreenSupplier(CraftsmanHouseBuildingScreen::new)
               .build();

   public static BuildingType CROP_FARM =
         BuildingType.builder(createResourceKey("crop_farm"))
               .isWorksite(true)
               .createState(CropFarmState::new)
               .buildingScreenSupplier(CropFarmBuildingScreen::new)
               .build();

   public static BuildingType COW_FARM =
         BuildingType.builder(createResourceKey("cow_farm"))
               .isWorksite(true)
               .isAnimalFarm(true)
               .createState(CowFarmState::new)
               .buildingScreenSupplier(AnimalFarmBuildingScreen::new)
               .build();

   public static BuildingType SHEEP_FARM =
         BuildingType.builder(createResourceKey("sheep_farm"))
               .isWorksite(true)
               .isAnimalFarm(true)
               .createState(SheepFarmState::new)
               .buildingScreenSupplier(AnimalFarmBuildingScreen::new)
               .build();

   public static BuildingType PIG_FARM =
         BuildingType.builder(createResourceKey("pig_farm"))
               .isWorksite(true)
               .isAnimalFarm(true)
               .createState(PigFarmState::new)
               .buildingScreenSupplier(AnimalFarmBuildingScreen::new)
               .build();

   public static BuildingType CHICKEN_FARM =
         BuildingType.builder(createResourceKey("chicken_farm"))
               .isWorksite(true)
               .isAnimalFarm(true)
               .createState(ChickenFarmState::new)
               .buildingScreenSupplier(AnimalFarmBuildingScreen::new)
               .build();

   public static BuildingType BEE_FARM =
         BuildingType.builder(createResourceKey("bee_farm"))
               .isWorksite(true)
               .isAnimalFarm(true)
               .createState(BeeFarmState::new)
               .buildingScreenSupplier(AnimalFarmBuildingScreen::new)
               .build();
   public static BuildingType FISHING_SPOT =
         BuildingType.builder(createResourceKey("fishing_spot"))
               .isWorksite(true)
               .buildingScreenSupplier(WorksiteBuildingScreen::new)
               .build();
   public static BuildingType MINE =
         BuildingType.builder(createResourceKey("mine"))
               .isWorksite(true)
               .buildingScreenSupplier(WorksiteBuildingScreen::new)
               .build();
   public static BuildingType QUARRY =
         BuildingType.builder(createResourceKey("quarry"))
               .isWorksite(true)
               .buildingScreenSupplier(WorksiteBuildingScreen::new)
               .build();
   public static BuildingType GROVE =
         BuildingType.builder(createResourceKey("grove"))
               .isWorksite(true)
               .createState(GroveState::new)
               .buildingScreenSupplier(GroveBuildingScreen::new)
               .build();
}
