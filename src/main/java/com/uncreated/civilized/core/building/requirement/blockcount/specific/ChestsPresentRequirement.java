package com.uncreated.civilized.core.building.requirement.blockcount.specific;

import com.uncreated.civilized.core.building.requirement.blockcount.BlockCountRequirement;
import com.uncreated.civilized.core.building.requirement.blockcount.validators.BlockClassValidator;

import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.AbstractChestBlock;

public class ChestsPresentRequirement extends BlockCountRequirement {
   public ChestsPresentRequirement(int requiredBlocks, boolean hideIfSatisfied) {
      super(
            new BlockClassValidator(AbstractChestBlock.class),
            requiredBlocks,
            Component.translatable("menu.building.management.requirements.count.description.chests"),
            hideIfSatisfied);
   }
}
