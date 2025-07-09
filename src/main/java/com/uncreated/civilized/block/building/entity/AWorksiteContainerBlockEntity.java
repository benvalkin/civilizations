package com.uncreated.civilized.block.building.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ContainerOpenersCounter;
import net.minecraft.world.level.block.state.BlockState;

public abstract class AWorksiteContainerBlockEntity extends AWorksiteBlockEntity {

   public AWorksiteContainerBlockEntity(BlockEntityType<?> blockEntityType, BlockPos pos, BlockState blockState) {
      super(blockEntityType, pos, blockState);
   }

   @Override
   public void saveAdditional(CompoundTag compound, HolderLookup.Provider registries) {
      super.saveAdditional(compound, registries);
      ContainerHelper.saveAllItems(compound, storageItems, registries);
      LOGGER.info("Saved storage items. First slot: " + storageItems.getFirst());
   }

   @Override
   public void loadAdditional(CompoundTag compound, HolderLookup.Provider registries) {
      super.loadAdditional(compound, registries);
      storageItems = NonNullList.withSize(getContainerSize(), ItemStack.EMPTY);
      ContainerHelper.loadAllItems(compound, storageItems, registries);
   }

   @Override
   protected NonNullList<ItemStack> getItems() {
      return storageItems;
   }

   @Override
   protected void setItems(NonNullList<ItemStack> items) {
      storageItems = items;
   }

   @Override
   protected AbstractContainerMenu createMenu(int id, Inventory playerInventory) {
      return ChestMenu.threeRows(id, playerInventory, this);
   }

   @Override
   public int getContainerSize() {
      return 27;
   }

   private ContainerOpenersCounter openersCounter = new ContainerOpenersCounter() {
      protected void onOpen(Level level, BlockPos pos, BlockState state) {
         // FarmerBlockEntity.this.playSound(state, ModSounds.BLOCK_CABINET_OPEN.get());
         AWorksiteContainerBlockEntity.this.updateBlockState(state, true);
      }

      protected void onClose(Level level, BlockPos pos, BlockState state) {
         // CabinetBlockEntity.this.playSound(state, ModSounds.BLOCK_CABINET_CLOSE.get());
         AWorksiteContainerBlockEntity.this.updateBlockState(state, false);
      }

      protected void openerCountChanged(Level level, BlockPos pos, BlockState sta, int arg1, int arg2) {
      }

      protected boolean isOwnContainer(Player player) {
         if (player.containerMenu instanceof ChestMenu) {
            Container container = ((ChestMenu) player.containerMenu).getContainer();
            return container == AWorksiteContainerBlockEntity.this;
         } else {
            return false;
         }
      }
   };

   public void startOpen(Player pPlayer) {
      if (level != null && !this.remove && !pPlayer.isSpectator()) {
         this.openersCounter.incrementOpeners(pPlayer, level, this.getBlockPos(), this.getBlockState());
      }
   }

   public void stopOpen(Player pPlayer) {
      if (level != null && !this.remove && !pPlayer.isSpectator()) {
         this.openersCounter.decrementOpeners(pPlayer, level, this.getBlockPos(), this.getBlockState());
      }
   }

   public void recheckOpen() {
      if (level != null && !this.remove) {
         this.openersCounter.recheckOpeners(level, this.getBlockPos(), this.getBlockState());
      }
   }

   void updateBlockState(BlockState state, boolean open) {
      if (level != null) {
         // this.level.setBlock(this.getBlockPos(), state.setValue(FarmerBlock.OPEN, open), 3);
      }
   }
}
