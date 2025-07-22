package com.uncreated.civilized.core.building.requirement.blockcount.specific;

import com.uncreated.civilized.core.building.requirement.blockcount.BlockCountRequirement;
import com.uncreated.civilized.core.building.requirement.blockcount.validators.BlockClassValidator;

import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.SignBlock;

public class SignsPresentRequirement extends BlockCountRequirement {
   public SignsPresentRequirement(int requiredBlocks, boolean hideIfSatisfied) {
      super(
            new BlockClassValidator(SignBlock.class),
            requiredBlocks,
            Component.translatable("menu.building.management.requirements.count.description.signs"),
            hideIfSatisfied);
   }
}
