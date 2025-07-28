package com.uncreated.civilized.core.building.requirement.registry;

import static com.uncreated.civilized.CivilizedMod.CIVILIZED_MOD_ID;

import java.util.Optional;

import com.uncreated.civilized.CivilizedMod;
import com.uncreated.civilized.core.building.BuildingType;
import com.uncreated.civilized.core.building.requirement.EnclosedWallsRequirement;
import com.uncreated.civilized.core.building.requirement.SpaceRequirement;
import com.uncreated.civilized.core.building.requirement.SurfaceAreaRequirement;
import com.uncreated.civilized.core.building.requirement.blockcount.BlockTypeRequirement;
import com.uncreated.civilized.core.building.requirement.blockcount.BuildingBlockTypes;
import com.uncreated.civilized.core.building.requirement.blockcount.specific.BedsPresentRequirement;
import com.uncreated.civilized.core.building.requirement.blockcount.specific.ChestsPresentRequirement;
import com.uncreated.civilized.core.building.requirement.blockcount.specific.SignsPresentRequirement;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegisterEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;

@EventBusSubscriber(modid = CivilizedMod.CIVILIZED_MOD_ID)
public class BuildingRequirementRegistry {

   private static final ResourceKey<Registry<BuildingRequirementList>> BUILDING_REQUIREMENTS_KEY =
         ResourceKey
               .createRegistryKey(ResourceLocation.fromNamespaceAndPath(CIVILIZED_MOD_ID, "building_requirements"));
   private static final Registry<BuildingRequirementList> BUILDING_REQUIREMENTS_INTERNAL =
         new RegistryBuilder<>(BUILDING_REQUIREMENTS_KEY).create();

   public static final DeferredRegister<BuildingRequirementList> BUILDING_REQUIREMENTS =
         DeferredRegister.create(BUILDING_REQUIREMENTS_INTERNAL, CIVILIZED_MOD_ID);

   @SubscribeEvent
   private static void registerRegistries(NewRegistryEvent event) {
      event.register(BUILDING_REQUIREMENTS_INTERNAL);
   }

   @SubscribeEvent
   private static void registerRequirements(RegisterEvent event) {
      event.register(BUILDING_REQUIREMENTS_KEY, registry -> {

         registerRequirements(
               registry,
               BuildingRequirementList.forBuilding(BuildingType.INN, 1)
                     .add(new SpaceRequirement(50))
                     .add(new EnclosedWallsRequirement())
                     .add(new BlockTypeRequirement(BuildingBlockTypes.WOOD, 200))
                     .add(new ChestsPresentRequirement(1, false))
                     .add(new BedsPresentRequirement(4, false))
                     .add(new SignsPresentRequirement(1, false))
                     .create());

         registerRequirements(
               registry,
               BuildingRequirementList.forBuilding(BuildingType.FARMER_HOUSE, 1)
                     .add(new SpaceRequirement(20))
                     .add(new EnclosedWallsRequirement())
                     .add(new BlockTypeRequirement(BuildingBlockTypes.WOOD, 100))
                     .add(new ChestsPresentRequirement(1, false))
                     .add(new SignsPresentRequirement(1, false))
                     .create());

         registerRequirements(
               registry,
               BuildingRequirementList.forBuilding(BuildingType.FARMER_HOUSE, 2)
                     .add(new SpaceRequirement(30))
                     .add(new EnclosedWallsRequirement())
                     .add(new BlockTypeRequirement(BuildingBlockTypes.WOOD, 160))
                     .add(new ChestsPresentRequirement(1, false))
                     .add(new SignsPresentRequirement(1, false))
                     .create());

         registerRequirements(
               registry,
               BuildingRequirementList.forBuilding(BuildingType.WOODCUTTER_HOUSE, 1)
                     .add(new SpaceRequirement(20))
                     .add(new EnclosedWallsRequirement())
                     .add(new BlockTypeRequirement(BuildingBlockTypes.WOOD, 100))
                     .add(new ChestsPresentRequirement(1, false))
                     .add(new SignsPresentRequirement(1, false))
                     .create());

         registerRequirements(
               registry,
               BuildingRequirementList.forBuilding(BuildingType.WOODCUTTER_HOUSE, 2)
                     .add(new SpaceRequirement(30))
                     .add(new EnclosedWallsRequirement())
                     .add(new BlockTypeRequirement(BuildingBlockTypes.WOOD, 160))
                     .add(new ChestsPresentRequirement(1, false))
                     .add(new SignsPresentRequirement(1, false))
                     .create());

         registerRequirements(
               registry,
               BuildingRequirementList.forBuilding(BuildingType.RANCHER_HOUSE, 1)
                     .add(new SpaceRequirement(20))
                     .add(new EnclosedWallsRequirement())
                     .add(new BlockTypeRequirement(BuildingBlockTypes.WOOD, 100))
                     .add(new ChestsPresentRequirement(1, false))
                     .add(new SignsPresentRequirement(1, false))
                     .create());

         registerRequirements(
               registry,
               BuildingRequirementList.forBuilding(BuildingType.RANCHER_HOUSE, 2)
                     .add(new SpaceRequirement(30))
                     .add(new EnclosedWallsRequirement())
                     .add(new BlockTypeRequirement(BuildingBlockTypes.WOOD, 160))
                     .add(new ChestsPresentRequirement(1, false))
                     .add(new SignsPresentRequirement(1, false))
                     .create());

         registerRequirements(
               registry,
               BuildingRequirementList.forBuilding(BuildingType.MINER_HOUSE, 1)
                     .add(new SpaceRequirement(20))
                     .add(new EnclosedWallsRequirement())
                     .add(new BlockTypeRequirement(BuildingBlockTypes.WOOD, 100))
                     .add(new ChestsPresentRequirement(1, false))
                     .add(new SignsPresentRequirement(1, false))
                     .create());

         registerRequirements(
               registry,
               BuildingRequirementList.forBuilding(BuildingType.MINER_HOUSE, 2)
                     .add(new SpaceRequirement(30))
                     .add(new EnclosedWallsRequirement())
                     .add(new BlockTypeRequirement(BuildingBlockTypes.WOOD, 160))
                     .add(new ChestsPresentRequirement(1, false))
                     .add(new SignsPresentRequirement(1, false))
                     .create());

         registerRequirements(
               registry,
               BuildingRequirementList.forBuilding(BuildingType.CROP_FARM, 1)
                     .add(new SurfaceAreaRequirement(40))
                     .add(new SignsPresentRequirement(1, false))
                     .create());

         registerRequirements(
               registry,
               BuildingRequirementList.forBuilding(BuildingType.GROVE, 1)
                     .add(new SurfaceAreaRequirement(80))
                     .add(new SignsPresentRequirement(1, false))
                     .create());

         registerRequirements(
               registry,
               BuildingRequirementList.forBuilding(BuildingType.CATTLE_FARM, 1)
                     .add(new SurfaceAreaRequirement(64))
                     .add(new SignsPresentRequirement(1, false))
                     .create());

         registerRequirements(
               registry,
               BuildingRequirementList.forBuilding(BuildingType.MINE, 1)
                     .add(new SurfaceAreaRequirement(9))
                     .add(new SignsPresentRequirement(1, false))
                     .create());
      });
   }

   private static ResourceLocation getResourceKey(BuildingType buildingType, int upgradeLevel) {
      return ResourceLocation
            .fromNamespaceAndPath(CIVILIZED_MOD_ID, buildingType.name().toLowerCase() + "_" + upgradeLevel);
   }

   private static BuildingRequirementList registerRequirements(
         RegisterEvent.RegisterHelper<BuildingRequirementList> registerHelper,
         BuildingRequirementList requirementList) {
      ResourceLocation resourceKey =
            getResourceKey(requirementList.getBuildingType(), requirementList.getUpgradeLevel());
      registerHelper.register(resourceKey, requirementList);
      return requirementList;
   }

   public static BuildingRequirementList getBuildingRequirements(BuildingType buildingType, int upgradeLevel) {
      Optional<BuildingRequirementList> requirements =
            BUILDING_REQUIREMENTS_INTERNAL.stream()
                  .filter(f -> f.getBuildingType() == buildingType && f.getUpgradeLevel() == upgradeLevel)
                  .findFirst();
      return requirements.orElseGet(() -> BuildingRequirementList.forBuilding(buildingType, 1).create());

   }
}
