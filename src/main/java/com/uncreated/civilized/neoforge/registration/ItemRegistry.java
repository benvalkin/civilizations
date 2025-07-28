package com.uncreated.civilized.neoforge.registration;

import static com.uncreated.civilized.CivilizedMod.CIVILIZED_MOD_ID;

import com.uncreated.civilized.core.building.BuildingType;
import com.uncreated.civilized.item.BuildingDeedItem;
import com.uncreated.civilized.item.CurrencyItem;
import com.uncreated.civilized.item.SettlementMandateItem;
import com.uncreated.civilized.neoforge.registration.entity.EntityRegistry;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ItemRegistry {
   // Create a Deferred Register to hold Items which will all be registered under the "civilized" namespace
   public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(CIVILIZED_MOD_ID);

   public static final DeferredItem<Item> INN =
         ITEMS.registerItem("inn", properties -> new BuildingDeedItem(properties.stacksTo(1), BuildingType.INN));
   public static final DeferredItem<Item> FARMER_HOUSE =
         ITEMS.registerItem(
               "farmer_house",
               properties -> new BuildingDeedItem(properties.stacksTo(1), BuildingType.FARMER_HOUSE));
   public static final DeferredItem<Item> WOODCUTTER_HOUSE =
         ITEMS.registerItem(
               "woodcutter_house",
               properties -> new BuildingDeedItem(properties.stacksTo(1), BuildingType.WOODCUTTER_HOUSE));
   public static final DeferredItem<Item> RANCHER_HOUSE =
         ITEMS.registerItem(
               "rancher_house",
               properties -> new BuildingDeedItem(properties.stacksTo(1), BuildingType.RANCHER_HOUSE));
   public static final DeferredItem<Item> MINER_HOUSE =
         ITEMS.registerItem(
               "miner_house",
               properties -> new BuildingDeedItem(properties.stacksTo(1), BuildingType.MINER_HOUSE));
   public static final DeferredItem<Item> CROP_FARM =
         ITEMS.registerItem(
               "crop_farm",
               properties -> new BuildingDeedItem(properties.stacksTo(1), BuildingType.CROP_FARM));
   public static final DeferredItem<Item> GROVE =
         ITEMS.registerItem("grove", properties -> new BuildingDeedItem(properties.stacksTo(1), BuildingType.GROVE));
   public static final DeferredItem<Item> MINE =
         ITEMS.registerItem("mine", properties -> new BuildingDeedItem(properties.stacksTo(1), BuildingType.MINE));
   public static final DeferredItem<Item> CATTLE_FARM =
         ITEMS.registerItem(
               "cattle_farm",
               properties -> new BuildingDeedItem(properties.stacksTo(1), BuildingType.CATTLE_FARM));

   public static final DeferredItem<Item> SETTLEMENT_MANDATE =
         ITEMS.registerItem("settlement_mandate", properties -> new SettlementMandateItem(properties.stacksTo(1)));

   public static final DeferredItem<Item> COIN =
         ITEMS.registerItem("coin", properties -> new CurrencyItem(properties.stacksTo(64), 1));

   public static final DeferredItem<Item> COIN_STACK =
         ITEMS.registerItem("coin_stack", properties -> new CurrencyItem(properties.stacksTo(64), 10));

   DeferredItem<SpawnEggItem> MY_ENTITY_SPAWN_EGG =
         ITEMS.registerItem(
               "my_entity_spawn_egg",
               properties -> new SpawnEggItem(
                     // The entity type to spawn.
                     EntityRegistry.CIVILIZED_VILLAGER.get(),
                     // The properties passed into the lambda, with any additional setup.
                     properties));
}
