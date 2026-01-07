package com.uncreated.civilized.core.building.requirement.blockcount.specific;

import com.uncreated.civilized.core.building.requirement.blockcount.BlockCountRequirement;
import com.uncreated.civilized.core.building.requirement.blockcount.validators.BlockClassValidator;

import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.AbstractChestBlock;
import net.minecraft.world.level.block.AbstractFurnaceBlock;

public class FurnacesPresentRequirement extends BlockCountRequirement {
   public FurnacesPresentRequirement(int requiredBlocks, boolean hideIfSatisfied) {
      super(
            new BlockClassValidator(AbstractFurnaceBlock.class),
            requiredBlocks,
            Component.translatable("menu.building.management.requirements.count.description.furnaces"),
            hideIfSatisfied);
   }
}
