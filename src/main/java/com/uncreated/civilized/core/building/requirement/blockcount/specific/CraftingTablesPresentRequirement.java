package com.uncreated.civilized.core.building.requirement.blockcount.specific;

import com.uncreated.civilized.core.building.requirement.blockcount.BlockCountRequirement;
import com.uncreated.civilized.core.building.requirement.blockcount.validators.BlockClassValidator;

import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.CraftingTableBlock;

public class CraftingTablesPresentRequirement extends BlockCountRequirement {
   public CraftingTablesPresentRequirement(int requiredBlocks, boolean hideIfSatisfied) {
      super(
            new BlockClassValidator(CraftingTableBlock.class),
            requiredBlocks,
            Component.translatable("menu.building.management.requirements.count.description.crafting_tables"),
            hideIfSatisfied);
   }
}
