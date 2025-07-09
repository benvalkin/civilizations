package com.uncreated.civilized.neoforge.registration;

import static com.uncreated.civilized.CivilizedMod.CIVILIZED_MOD_ID;

import com.uncreated.civilized.core.building.BuildingType;
import com.uncreated.civilized.item.BuildingDeedItem;
import com.uncreated.civilized.item.SettlementMandateItem;
import com.uncreated.civilized.neoforge.registration.entity.EntityRegistry;

import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ItemRegistry {
   // Create a Deferred Register to hold Items which will all be registered under the "civilized" namespace
   public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(CIVILIZED_MOD_ID);

   // Creates a new food item with the id "civilized:example_id", nutrition 1 and saturation 2
   public static final DeferredItem<Item> EXAMPLE_ITEM =
         ITEMS.registerSimpleItem(
               "example_item",
               new Item.Properties()
                     .food(new FoodProperties.Builder().alwaysEdible().nutrition(1).saturationModifier(2f).build()));

   public static final DeferredItem<Item> INN =
         ITEMS.registerItem("inn", properties -> new BuildingDeedItem(properties.stacksTo(1), BuildingType.INN));
   public static final DeferredItem<Item> FARMER_HOUSE =
         ITEMS.registerItem(
               "farmer_house",
               properties -> new BuildingDeedItem(properties.stacksTo(1), BuildingType.FARMER_HOUSE));
   public static final DeferredItem<Item> SETTLEMENT_MANDATE =
         ITEMS.registerItem("settlement_mandate", properties -> new SettlementMandateItem(properties.stacksTo(1)));

   DeferredItem<SpawnEggItem> MY_ENTITY_SPAWN_EGG =
         ITEMS.registerItem(
               "my_entity_spawn_egg",
               properties -> new SpawnEggItem(
                     // The entity type to spawn.
                     EntityRegistry.CIVILIZED_VILLAGER.get(),
                     // The properties passed into the lambda, with any additional setup.
                     properties));
}
