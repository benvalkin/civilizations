package com.uncreated.civilized.tag;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

/**
 * References to tags under the Forge namespace. These tags are generally used for crafting recipes across different
 * mods.
 */
public class CommonTags {
   // Farmer's Delight in mind
   public static final TagKey<Item> CROPS_GRAIN = commonItemTag("crops/grain");
   public static final TagKey<Item> FOODS_DOUGH = commonItemTag("foods/dough");
   public static final TagKey<Item> FOODS_DOUGH_WHEAT = commonItemTag("foods/dough/wheat");
   public static final TagKey<Item> FOODS_PASTA = commonItemTag("foods/pasta");
   // Create flour in mind
   public static final TagKey<Item> FLOURS = commonItemTag("flours");
   public static final TagKey<Item> FLOURS_WHEAT = commonItemTag("flours/wheat");

   private static TagKey<Block> commonBlockTag(String path) {
      return BlockTags.create(ResourceLocation.fromNamespaceAndPath("c", path));
   }

   private static TagKey<Item> commonItemTag(String path) {
      return ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", path));
   }
}
