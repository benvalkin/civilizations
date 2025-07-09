package com.uncreated.civilized.block.building.requirement.blocktype;

import java.util.List;

import com.uncreated.civilized.block.building.requirement.BlockTypeRequirement;
import com.uncreated.civilized.ui.style.Colors;

import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;

public class BuildingBlockTypes {
   public static final BlockTypeRequirement.BuildingBlockType WOOD =
         new BlockTypeRequirement.BuildingBlockType(
               Component.translatable("menu.building.management.requirements.block_types.type.wood")
                     .withColor(Colors.RESOURCE_WOOD),
               List.of(BlockTags.LOGS, BlockTags.PLANKS, BlockTags.WOODEN_SLABS, BlockTags.WOODEN_STAIRS));
}
