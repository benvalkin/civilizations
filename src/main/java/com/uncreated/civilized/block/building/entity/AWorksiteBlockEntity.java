package com.uncreated.civilized.block.building.entity;

import java.util.UUID;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;
import com.uncreated.civilized.block.building.CropFarmBlock;
import com.uncreated.civilized.core.building.Building;
import com.uncreated.civilized.core.building.BuildingType;
import com.uncreated.civilized.core.building.ServerBuildingsStore;
import com.uncreated.civilized.core.building.bounds.BuildingBounds;
import com.uncreated.civilized.core.settlement.ServerSettlementsStore;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class AWorksiteBlockEntity extends BaseContainerBlockEntity {

   public static final Logger LOGGER = LogUtils.getLogger();

   public AWorksiteBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
      super(type, pos, blockState);
   }

   @Override
   public void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
      super.loadAdditional(tag, registries);
   }

   @Override
   public void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
      super.saveAdditional(tag, registries);
   }

   // === SYNCING ON CHUNK LOAD ===
   @Override
   public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
      CompoundTag tag = new CompoundTag();
      saveAdditional(tag, registries);
      return tag;
   }

   @Override
   public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider registries) {
      super.handleUpdateTag(tag, registries);
      LOGGER.info("Received update tag: {}", tag);
   }

   // === SYNCING ON BLOCK LOAD ===
   @Override
   public Packet<ClientGamePacketListener> getUpdatePacket() {
      // The packet uses the CompoundTag returned by #getUpdateTag. An alternative overload of #create exists
      // that allows you to specify a custom update tag, including the ability to omit data the client might not need.
      return ClientboundBlockEntityDataPacket.create(this);
   }

   @Override
   public void onDataPacket(
         Connection connection,
         ClientboundBlockEntityDataPacket packet,
         HolderLookup.Provider registries) {
      // client sets extra data received by server
      super.onDataPacket(connection, packet, registries); // super calls loadsAdditional
   }

   /**
    * Called on the SERVER to
    *
    * @param placerId
    *           The UUID of the player entity who placed the block.
    */
   public void serverRegisterBuilding(UUID placerId) {

      var settlement =
            ServerSettlementsStore.INSTANCE.findFromOwner(placerId)
                  .orElse(ServerSettlementsStore.INSTANCE.createNew(placerId));

      Building building =
            ServerBuildingsStore.INSTANCE.createNew(
                  level.registryAccess(),
                  level.dimension(),
                  settlement.getSettlementId(),
                  placerId,
                  getBuildingType(),
                  BuildingBounds.singleBlock(getBlockPos()));

      LOGGER.info("Registered new building {} ({}).", building.getBuildingType(), building.getBuildingId());

      triggerBlockUpdate();
   }

   public abstract @NotNull BuildingType getBuildingType();

   public boolean triggerBlockUpdate() {

      if (level == null)
         return false;

      level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), CropFarmBlock.UPDATE_CLIENTS);
      return true;
   }

   @Override
   protected Component getDefaultName() {
      return Component.literal(getBuildingType().name());
   }

   protected NonNullList<ItemStack> storageItems = NonNullList.withSize(27, ItemStack.EMPTY);

   @Override
   protected NonNullList<ItemStack> getItems() {
      return storageItems;
   }

   @Override
   protected void setItems(NonNullList<ItemStack> items) {
      storageItems = items;
   }

   @Override
   protected AbstractContainerMenu createMenu(int i, Inventory inventory) {
      return null;
   }

   @Override
   public int getContainerSize() {
      return 0;
   }
}
