package com.uncreated.civilized.core.building.requirement.blockcount.validators;

import com.uncreated.civilized.core.building.requirement.blockcount.BlockCountRequirement;
import com.uncreated.civilized.core.building.requirement.blockcount.BuildingBlockTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class BlockTypeValidator implements BlockCountRequirement.IBlockValidator {

   public final BuildingBlockTypes.BuildingBlockType type;

    public BlockTypeValidator(BuildingBlockTypes.BuildingBlockType type) {
        this.type = type;
    }

    @Override
   public boolean isBlockValid(BlockPos pos, Level level) {

      BlockState blockState = level.getBlockState(pos);

      return blockState.getTags().anyMatch(type.getAllowedBlockTags()::contains);
   }
}
