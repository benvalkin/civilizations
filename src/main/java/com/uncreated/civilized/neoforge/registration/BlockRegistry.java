package com.uncreated.civilized.neoforge.registration;

import static com.uncreated.civilized.CivilizedMod.CIVILIZED_MOD_ID;
import static com.uncreated.civilized.neoforge.registration.ItemRegistry.ITEMS;

import java.util.function.Supplier;

import com.uncreated.civilized.block.building.CropFarmBlock;
import com.uncreated.civilized.block.building.entity.CropFarmBlockEntity;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class BlockRegistry {
   // Create a Deferred Register to hold Blocks which will all be registered under the "civilized" namespace
   public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(CIVILIZED_MOD_ID);

   public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
         DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, CIVILIZED_MOD_ID);

   // Creates a new Block with the id "civilized:example_block", combining the namespace and path
   public static final DeferredBlock<Block> EXAMPLE_BLOCK =
         BLOCKS.registerSimpleBlock("example_block", BlockBehaviour.Properties.of().mapColor(MapColor.STONE));
   // Creates a new BlockItem with the id "civilized:example_block", combining the namespace and path
   public static final DeferredItem<BlockItem> EXAMPLE_BLOCK_ITEM =
         ITEMS.registerSimpleBlockItem("example_block", EXAMPLE_BLOCK);

   // // Town Hall Block
   // public static final DeferredBlock<Block> TOWN_HALL_BLOCK =
   // BLOCKS.registerBlock("town_hall_block", p -> new TownHallBlock(p.noOcclusion()));
   // public static final DeferredItem<BlockItem> TOWN_HALL_BLOCK_ITEM =
   // ITEMS.registerSimpleBlockItem("town_hall_block", TOWN_HALL_BLOCK);
   // public static final Supplier<BlockEntityType<TownHallBlockEntity>> TOWN_HALL_BLOCK_ENTITY =
   // BLOCK_ENTITIES.register(
   // "town_hall_block_entity",
   // // The block entity type.
   // () -> new BlockEntityType<>(
   // // The supplier to use for constructing the block entity instances.
   // TownHallBlockEntity::new,
   // // A vararg of blocks that can have this block entity.
   // // This assumes the existence of the referenced blocks as DeferredBlock<Block>s.
   // TOWN_HALL_BLOCK.get()));

   public static final DeferredBlock<Block> CROP_FARM_BLOCK =
         BLOCKS.registerBlock("crop_farm_block", p -> new CropFarmBlock(p));
   public static final DeferredItem<BlockItem> CROP_FARM_BLOCK_ITEM =
         ITEMS.registerSimpleBlockItem("crop_farm_block", CROP_FARM_BLOCK);
   public static final Supplier<BlockEntityType<CropFarmBlockEntity>> CROP_FARM_BLOCK_ENTITY =
         BLOCK_ENTITIES.register(
               "crop_farm_block",
               () -> new BlockEntityType<>(CropFarmBlockEntity::new, CROP_FARM_BLOCK.get()));
}
