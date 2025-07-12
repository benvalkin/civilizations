package com.uncreated.civilized.block.building.requirement.blockcount;

import com.uncreated.civilized.block.building.requirement.blockcount.validators.BlockTagValidator;
import com.uncreated.civilized.ui.style.Colors;

import net.minecraft.network.chat.Component;

public class BlockTypeRequirement extends BlockCountRequirement {
   private final BuildingBlockTypes.BuildingBlockType type;

   public BlockTypeRequirement(BuildingBlockTypes.BuildingBlockType type) {
      super(new BlockTagValidator(type), type.getDescription(), false);
      this.type = type;
   }

   @Override
   public BlockCountResult createResult(int actualBlocks, int requiredBlocks) {
      return new Result(actualBlocks, requiredBlocks);
   }

   @Override
   public boolean haltChecksIfBlockCanSeeSky() {
      return true;
   }

   public class Result extends BlockCountResult {

      public Result(int actualBlocks, int requiredBlocks) {
         super(actualBlocks, requiredBlocks);
      }

      @Override
      public Component getDescription() {
         int numberToDisplay = Math.clamp(actualBlocks, 0, requiredBlocks);
         return Component
               .translatable(
                     "menu.building.management.requirements.count.description",
                     type.getDescription(),
                     numberToDisplay,
                     requiredBlocks)
               .withColor(Colors.MENU_TEXT_DARK);
      }

      @Override
      public Component getTooltipDescription() {
         return Component.translatable(
               "menu.building.management.requirements.count.tooltip",
               requiredBlocks,
               type.getDescription());
      }
   }
}
