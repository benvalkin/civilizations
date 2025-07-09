package com.uncreated.civilized.block.building;

import com.uncreated.civilized.block.building.entity.AWorksiteContainerBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public abstract class AWorksiteContainerBlock extends AWorksiteBlock {

   public AWorksiteContainerBlock(Properties properties) {
      super(properties);
   }

   @Override
   protected InteractionResult useWithoutItem(
         BlockState state,
         Level level,
         BlockPos pos,
         Player player,
         BlockHitResult hitResult) {
      if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
         BlockEntity blockEntity = level.getBlockEntity(pos);
         if (blockEntity instanceof AWorksiteContainerBlockEntity be) {
            // serverPlayer.openMenu(state.getMenuProvider(level, pos));
            player.openMenu(be);
         }
      }

      return InteractionResult.SUCCESS;
   }

   @Override
   public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
      if (state.getBlock() != newState.getBlock()) {
         BlockEntity tileEntity = level.getBlockEntity(pos);
         if (tileEntity instanceof Container) {
            Containers.dropContents(level, pos, (Container) tileEntity);
            level.updateNeighbourForOutputSignal(pos, this);
         }
         super.onRemove(state, level, pos, newState, isMoving);
      }
   }

   @Override
   public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
      BlockEntity tileEntity = level.getBlockEntity(pos);
      if (tileEntity instanceof AWorksiteContainerBlockEntity be) {
         be.recheckOpen();
      }
   }
}
