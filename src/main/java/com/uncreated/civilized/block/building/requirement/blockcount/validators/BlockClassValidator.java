package com.uncreated.civilized.block.building.requirement.blockcount.validators;

import com.uncreated.civilized.block.building.requirement.blockcount.BlockCountRequirement;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class BlockClassValidator implements BlockCountRequirement.IBlockValidator {

    private final Class<? extends Block> blockClass;

    public BlockClassValidator(Class<? extends Block> blockClass) {
        this.blockClass = blockClass;
    }

    @Override
    public boolean isBlockValid(BlockPos pos, Level level) {
        BlockState state = level.getBlockState(pos);
        return blockClass.isInstance(state.getBlock());
    }
}
