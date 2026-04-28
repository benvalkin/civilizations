package com.uncreated.civilized.core.building.production.bills;

import static com.uncreated.civilized.CivilizedMod.CIVILIZED_MOD_ID;

import com.uncreated.civilized.CivilizedMod;
import com.uncreated.civilized.core.building.production.lines.crafting.CraftingMachine;
import com.uncreated.civilized.core.building.production.lines.singleitem.cooking.BlastingMachine;
import com.uncreated.civilized.core.building.production.lines.singleitem.cooking.SmeltingMachine;
import com.uncreated.civilized.core.building.production.lines.singleitem.cooking.SmokingMachine;
import com.uncreated.civilized.ui.menu.building.residence.artisan.crafting.EditCraftingRecipeMenu;
import com.uncreated.civilized.ui.menu.building.residence.artisan.singleitem.EditBlastingRecipeMenu;
import com.uncreated.civilized.ui.menu.building.residence.artisan.singleitem.EditSmeltingRecipeMenu;
import com.uncreated.civilized.ui.menu.building.residence.artisan.singleitem.EditSmokingRecipeMenu;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegisterEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;

@EventBusSubscriber(modid = CivilizedMod.CIVILIZED_MOD_ID)
public class ProductionTypes {
   private static final ResourceKey<Registry<ProductionType>> PRODUCTION_TYPES_KEY =
         ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(CIVILIZED_MOD_ID, "production_types"));
   private static final Registry<ProductionType> PRODUCTION_TYPES_INTERNAL =
         new RegistryBuilder<>(PRODUCTION_TYPES_KEY).create();

   public static final DeferredRegister<ProductionType> PRODUCTION_TYPES =
         DeferredRegister.create(PRODUCTION_TYPES_INTERNAL, CIVILIZED_MOD_ID);

   public static ResourceLocation createResourceKey(String villagerOccupationName) {
      return ResourceLocation.fromNamespaceAndPath(CIVILIZED_MOD_ID, villagerOccupationName);
   }

   private static void registerProductionType(
         RegisterEvent.RegisterHelper<ProductionType> registry,
         ProductionType productionType) {
      registry.register(productionType.resourceLocation(), productionType);
   }

   public static ProductionType getFromResourceLocation(ResourceLocation resourceLocation) {
      return PRODUCTION_TYPES.getRegistry().get().getValue(resourceLocation);
   }

   @SubscribeEvent
   private static void registerRegistry(NewRegistryEvent event) {
      event.register(PRODUCTION_TYPES_INTERNAL);
   }

   @SubscribeEvent
   private static void registerTypes(RegisterEvent event) {
      event.register(PRODUCTION_TYPES_KEY, registry -> {
         registerProductionType(registry, CRAFTING);
         registerProductionType(registry, SMELTING);
         registerProductionType(registry, BLASTING);
         registerProductionType(registry, SMOKING);
      });
   }

   public static final ProductionType CRAFTING =
         new ProductionType(
               createResourceKey("crafting_production"),
               RecipeType.CRAFTING,
               CraftingMachine::new,
               (
                     containerId,
                     playerInventory,
                     settlement,
                     building,
                     productionBillIndex,
                     isNewBill) -> new EditCraftingRecipeMenu(
                           containerId,
                           playerInventory,
                           new SimpleContainer(9),
                           settlement,
                           building,
                           productionBillIndex,
                           isNewBill));
   public static final ProductionType SMELTING =
         new ProductionType(
               createResourceKey("smelting_production"),
               RecipeType.SMELTING,
               SmeltingMachine::new,
               (
                     containerId,
                     playerInventory,
                     settlement,
                     building,
                     productionBillIndex,
                     isNewBill) -> new EditSmeltingRecipeMenu(
                           containerId,
                           playerInventory,
                           new SimpleContainer(9),
                           settlement,
                           building,
                           productionBillIndex,
                           isNewBill));
   public static final ProductionType BLASTING =
         new ProductionType(
               createResourceKey("blasting_production"),
               RecipeType.BLASTING,
               BlastingMachine::new,
               (
                     containerId,
                     playerInventory,
                     settlement,
                     building,
                     productionBillIndex,
                     isNewBill) -> new EditBlastingRecipeMenu(
                           containerId,
                           playerInventory,
                           new SimpleContainer(9),
                           settlement,
                           building,
                           productionBillIndex,
                           isNewBill));
   public static final ProductionType SMOKING =
         new ProductionType(
               createResourceKey("smoking_production"),
               RecipeType.SMOKING,
               SmokingMachine::new,
               (
                     containerId,
                     playerInventory,
                     settlement,
                     building,
                     productionBillIndex,
                     isNewBill) -> new EditSmokingRecipeMenu(
                           containerId,
                           playerInventory,
                           new SimpleContainer(9),
                           settlement,
                           building,
                           productionBillIndex,
                           isNewBill));
}
