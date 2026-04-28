package com.uncreated.civilized.core.building.requirement.blockcount.specific;

import com.uncreated.civilized.core.building.requirement.blockcount.BlockCountRequirement;
import com.uncreated.civilized.core.building.requirement.blockcount.validators.BlockClassValidator;

import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.SmokerBlock;

public class SmokersPresentRequirement extends BlockCountRequirement {
   public SmokersPresentRequirement(int requiredBlocks, boolean hideIfSatisfied) {
      super(
            new BlockClassValidator(SmokerBlock.class),
            requiredBlocks,
            Component.translatable("menu.building.management.requirements.count.description.smokers"),
            hideIfSatisfied);
   }
}
