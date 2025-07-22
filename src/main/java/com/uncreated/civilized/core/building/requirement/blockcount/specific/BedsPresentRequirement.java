package com.uncreated.civilized.core.building.requirement.blockcount.specific;

import java.util.Collections;

import com.uncreated.civilized.core.building.requirement.blockcount.BlockCountRequirement;
import com.uncreated.civilized.core.building.requirement.blockcount.validators.BlockTagValidator;

import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;

public class BedsPresentRequirement extends BlockCountRequirement {
   public BedsPresentRequirement(int requiredBlocks, boolean hideIfSatisfied) {
      super(
            new BlockTagValidator(Collections.singletonList(BlockTags.BEDS)),
            requiredBlocks,
            Component.translatable("menu.building.management.requirements.count.description.beds"),
            hideIfSatisfied);
   }
}
