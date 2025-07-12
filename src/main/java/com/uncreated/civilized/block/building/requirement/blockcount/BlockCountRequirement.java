package com.uncreated.civilized.block.building.requirement.blockcount;

import com.uncreated.civilized.block.building.requirement.IBuildingRequirement;
import com.uncreated.civilized.block.building.requirement.IBuildingRequirementResult;
import com.uncreated.civilized.core.building.bounds.BuildingBounds;
import com.uncreated.civilized.ui.style.Colors;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;

public class BlockCountRequirement implements IBuildingRequirement {

   protected final IBlockValidator validator;
   protected final Component blockDescription;
   private final boolean hidIfSatisfied;

   public BlockCountRequirement(IBlockValidator validator, Component blockDescription, boolean hideIfSatisfied) {
      this.validator = validator;
      this.blockDescription = blockDescription;
      this.hidIfSatisfied = hideIfSatisfied;
   }

   public BlockCountResult getResult(Level level, BuildingBounds bounds, int requiredBlocks) {
      int validBlocksFound = 0;
      BlockPos.MutableBlockPos current = bounds.getLowerCorner().mutable();
      for (int x = bounds.getLowerCorner().getX(); x <= bounds.getUpperCorner().getX(); x++) {
         for (int z = bounds.getLowerCorner().getZ(); z <= bounds.getUpperCorner().getZ(); z++) {
            for (int y = bounds.getLowerCorner().getY(); y <= bounds.getUpperCorner().getY(); y++) {
               current.set(x, y, z);

               if (validator.isBlockValid(current, level))
                  validBlocksFound++;

               if (haltChecksIfBlockCanSeeSky() && level.canSeeSky(current))
                  break;
            }
         }
      }
      return createResult(validBlocksFound, requiredBlocks);
   }

   public boolean haltChecksIfBlockCanSeeSky() {
      return false;
   }

   public BlockCountResult createResult(int actualBlocks, int requiredBlocks) {
      return new BlockCountResult(actualBlocks, requiredBlocks);
   }

   public class BlockCountResult implements IBuildingRequirementResult {
      protected final int actualBlocks;
      protected final int requiredBlocks;

      public BlockCountResult(int actualBlocks, int requiredBlocks) {
         this.actualBlocks = actualBlocks;
         this.requiredBlocks = requiredBlocks;
      }

      @Override
      public boolean isSatisfied() {
         return actualBlocks >= requiredBlocks;
      }

      @Override
      public Component getDescription() {
         int numberToDisplay = Math.clamp(actualBlocks, 0, requiredBlocks);
         return Component
               .translatable(
                     "menu.building.management.requirements.count.description",
                     blockDescription,
                     numberToDisplay,
                     requiredBlocks)
               .withColor(Colors.MENU_TEXT_DARK);
      }

      @Override
      public Component getTooltipDescription() {
         return Component
               .translatable("menu.building.management.requirements.count.tooltip", requiredBlocks, blockDescription);
      }

      @Override
      public boolean hideIfSatisfied() {
         return hidIfSatisfied;
      }
   }

   public interface IBlockValidator {
      boolean isBlockValid(BlockPos pos, Level level);
   }
}
