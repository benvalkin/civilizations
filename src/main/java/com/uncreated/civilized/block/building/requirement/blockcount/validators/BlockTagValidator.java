package com.uncreated.civilized.block.building.requirement.blockcount.validators;

import com.uncreated.civilized.block.building.requirement.blockcount.BlockCountRequirement;
import com.uncreated.civilized.block.building.requirement.blockcount.BuildingBlockTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class BlockTagValidator implements BlockCountRequirement.IBlockValidator {

   public final BuildingBlockTypes.BuildingBlockType type;

    public BlockTagValidator(BuildingBlockTypes.BuildingBlockType type) {
        this.type = type;
    }

    @Override
   public boolean isBlockValid(BlockPos pos, Level level) {

      BlockState blockState = level.getBlockState(pos);

      return blockState.getTags().anyMatch(type.getAllowedBlockTags()::contains);
   }
}
