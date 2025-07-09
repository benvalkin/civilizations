package com.uncreated.civilized.block.building.requirement;

import java.util.List;

import com.uncreated.civilized.core.building.bounds.BuildingBounds;
import com.uncreated.civilized.ui.style.Colors;

import lombok.Builder;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class BlockTypeRequirement implements IBuildingRequirement {

   private final BuildingBlockType type;

   public BlockTypeRequirement(BuildingBlockType type) {
      this.type = type;
   }

   public Result getResult(Level level, BuildingBounds bounds, int requiredBlocks) {
      int validBlocksFound = 0;
      BlockPos.MutableBlockPos current = bounds.getLowerCorner().mutable();
      for (int x = bounds.getLowerCorner().getX(); x <= bounds.getUpperCorner().getX(); x++) {
         for (int z = bounds.getLowerCorner().getZ(); z <= bounds.getUpperCorner().getZ(); z++) {
            for (int y = bounds.getLowerCorner().getY(); y <= bounds.getUpperCorner().getY(); y++) {
               current.set(x, y, z);

               BlockState blockState = level.getBlockState(current);

               boolean hasMatchingTag = blockState.getTags().anyMatch(type.allowedBlockTags::contains);
               if (hasMatchingTag)
                  validBlocksFound++;

               if (level.canSeeSky(current))
                  break;
            }
         }
      }
      return new Result(validBlocksFound, requiredBlocks);
   }

   public class Result implements IBuildingRequirementResult {
      private final int actualBlocks;
      private final int requiredBlocks;

      public Result(int actualBlocks, int requiredBlocks) {
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
                     "menu.building.management.requirements.block_types.description",
                     type.name,
                     numberToDisplay,
                     requiredBlocks)
               .withColor(Colors.MENU_TEXT_DARK);
      }

      @Override
      public Component getTooltipDescription() {
         return Component
               .translatable("menu.building.management.requirements.block_types.tooltip", requiredBlocks, type.name);
      }
   }

   @Builder
   public static class BuildingBlockType {
      private final Component name;
      private final List<TagKey<Block>> allowedBlockTags;

      public BuildingBlockType(Component name, List<TagKey<Block>> allowedBlockTags) {
         this.name = name;
         this.allowedBlockTags = allowedBlockTags;
      }
   }
}
