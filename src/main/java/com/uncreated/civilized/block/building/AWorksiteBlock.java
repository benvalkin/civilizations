package com.uncreated.civilized.block.building;

import javax.annotation.Nullable;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;
import com.uncreated.civilized.block.building.entity.AWorksiteBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;

public abstract class AWorksiteBlock extends BaseEntityBlock {

   public static final EnumProperty<Direction> FACING = HorizontalDirectionalBlock.FACING;
   protected static final Logger LOGGER = LogUtils.getLogger();

   public AWorksiteBlock(Properties properties) {
      super(properties);

      this.registerDefaultState(this.defaultBlockState().setValue(FACING, Direction.NORTH));
   }

   @Override
   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
      builder.add(FACING);
   }

   @Nullable
   @Override
   public BlockState getStateForPlacement(final BlockPlaceContext context) {
      @NotNull
      final Direction facing =
            (context.getPlayer() == null) ? Direction.NORTH : Direction.fromYRot(context.getPlayer().getYRot());
      return this.defaultBlockState().setValue(FACING, facing);
   }

   @NotNull
   @Override
   public BlockState rotate(final BlockState state, final Rotation rot) {
      return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
   }

   @Override
   public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
      return newBuildingBlockEntity(blockPos, blockState);
   }

   @Override
   public void destroy(LevelAccessor level, BlockPos pos, BlockState state) {
      if (level.isClientSide())
         return;

      // ServerBuildingsStore.INSTANCE.delete(pos);
   }

   public abstract @NotNull AWorksiteBlockEntity newBuildingBlockEntity(BlockPos blockPos, BlockState blockState);

   @Override
   public void setPlacedBy(
         Level level,
         BlockPos blockPos,
         BlockState blockState,
         @Nullable LivingEntity entity,
         ItemStack itemStack) {
      super.setPlacedBy(level, blockPos, blockState, entity, itemStack);

      if (level.isClientSide || !(entity instanceof Player))
         return;

      BlockEntity tileEntity = level.getBlockEntity(blockPos);
      if (!(tileEntity instanceof AWorksiteBlockEntity buildingBlockEntity))
         return;

      buildingBlockEntity.serverRegisterBuilding(entity.getUUID());
   }
}
