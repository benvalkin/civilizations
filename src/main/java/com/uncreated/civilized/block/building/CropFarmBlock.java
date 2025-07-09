package com.uncreated.civilized.block.building;

import org.jetbrains.annotations.NotNull;

import com.uncreated.civilized.block.building.entity.AWorksiteBlockEntity;
import com.uncreated.civilized.block.building.entity.CropFarmBlockEntity;
import com.mojang.serialization.MapCodec;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class CropFarmBlock extends AWorksiteBlock {

   public static final MapCodec<CropFarmBlock> CODEC = simpleCodec(CropFarmBlock::new);

   public CropFarmBlock(Properties properties) {
      super(properties);
   }

   @Override
   protected MapCodec<? extends BaseEntityBlock> codec() {
      return CODEC;
   }

   @Override
   public @NotNull AWorksiteBlockEntity newBuildingBlockEntity(BlockPos blockPos, BlockState blockState) {
      return new CropFarmBlockEntity(blockPos, blockState);
   }

   // @Override
   // public MenuProvider getMenuProvider(BlockState state, Level level, BlockPos pos) {
   //
   // return new SimpleMenuProvider(new MenuConstructor() {
   // @Override
   // public @Nullable AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
   // // return new MyMenu(i, inventory, new FriendlyByteBuf(Unpooled.copiedBuffer(new byte[0])));
   // return ChestMenu.threeRows(i, inventory, );
   // }
   // }, Component.literal("My Menu"));
   // }

   @Override
   protected InteractionResult useWithoutItem(
         BlockState state,
         Level level,
         BlockPos pos,
         Player player,
         BlockHitResult hitResult) {
      if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
//          serverPlayer.openMenu(state.getMenuProvider(level, pos));
         BlockEntity blockEntity = level.getBlockEntity(pos);
         if (blockEntity instanceof CropFarmBlockEntity fb) {
            player.openMenu(fb);
         }
      }

      return InteractionResult.SUCCESS;
   }
}
