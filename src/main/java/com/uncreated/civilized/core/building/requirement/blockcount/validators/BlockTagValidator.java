package com.uncreated.civilized.core.building.requirement.blockcount.validators;

import com.uncreated.civilized.core.building.requirement.blockcount.BlockCountRequirement;
import com.uncreated.civilized.core.building.requirement.blockcount.BuildingBlockTypes;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class BlockTagValidator implements BlockCountRequirement.IBlockValidator {

   public final List<TagKey<Block>> blockTags;

    public BlockTagValidator(List<TagKey<Block>> blockTags) {
        this.blockTags = blockTags;
    }

    @Override
   public boolean isBlockValid(BlockPos pos, Level level) {

      BlockState blockState = level.getBlockState(pos);

      return blockState.getTags().anyMatch(blockTags::contains);
   }
}
