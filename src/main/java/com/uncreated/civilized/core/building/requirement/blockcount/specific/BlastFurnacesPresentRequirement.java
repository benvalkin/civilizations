package com.uncreated.civilized.core.building.requirement.blockcount.specific;

import com.uncreated.civilized.core.building.requirement.blockcount.BlockCountRequirement;
import com.uncreated.civilized.core.building.requirement.blockcount.validators.BlockClassValidator;

import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.FurnaceBlock;

public class BlastFurnacesPresentRequirement extends BlockCountRequirement {
   public BlastFurnacesPresentRequirement(int requiredBlocks, boolean hideIfSatisfied) {
      super(
            new BlockClassValidator(FurnaceBlock.class),
            requiredBlocks,
            Component.translatable("menu.building.management.requirements.count.description.blast_furnaces"),
            hideIfSatisfied);
   }
}
