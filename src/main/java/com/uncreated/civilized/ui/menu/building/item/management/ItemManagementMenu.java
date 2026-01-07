package com.uncreated.civilized.ui.menu.building.item.management;

import com.uncreated.civilized.core.StoreOperation;
import com.uncreated.civilized.core.building.Building;
import com.uncreated.civilized.core.building.ServerBuildingsStore;
import com.uncreated.civilized.core.building.state.CropFarmState;
import com.uncreated.civilized.core.settlement.Settlement;
import com.uncreated.civilized.networking.packets.ShowBuildingScreen;

import lombok.Getter;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;

public abstract class ItemManagementMenu extends AbstractContainerMenu {
   @Getter
   protected Container container;
   @Getter
   protected Settlement settlement;
   @Getter
   protected Building building;

   public ItemManagementMenu(MenuType<? extends ItemManagementMenu> chooseSaplingsMenuMenuType, int containerId) {
      super(chooseSaplingsMenuMenuType, containerId);
   }

   @Override
   public boolean stillValid(Player player) {
      return this.container.stillValid(player);
   }

   public ItemStack quickMoveStack(Player player, int index) {
      ItemStack itemstack = ItemStack.EMPTY;
      Slot slot = this.slots.get(index);
      if (slot != null && slot.hasItem()) {
         ItemStack itemstack1 = slot.getItem();
         itemstack = itemstack1.copy();
         if (index < this.container.getContainerSize()) {
            if (!this.moveItemStackTo(itemstack1, this.container.getContainerSize(), this.slots.size(), true)) {
               return ItemStack.EMPTY;
            }
         } else if (!this.moveItemStackTo(itemstack1, 0, this.container.getContainerSize(), false)) {
            return ItemStack.EMPTY;
         }

         if (itemstack1.isEmpty()) {
            slot.setByPlayer(ItemStack.EMPTY);
         } else {
            slot.setChanged();
         }
      }

      return itemstack;
   }

   @Override
   public void removed(Player player) {
      super.removed(player);
      this.container.stopOpen(player);

      if (!(player instanceof ServerPlayer serverPlayer))
         return;

      if (!(building.getState() instanceof CropFarmState cropFarmBehaviour))
         return;

      cropFarmBehaviour.setCropSlot(0, container.getItem(0));
      cropFarmBehaviour.setCropSlot(1, container.getItem(1));
      cropFarmBehaviour.setCropSlot(2, container.getItem(2));

      ServerBuildingsStore.INSTANCE.replicateChange(building, StoreOperation.UPDATE);
      ServerBuildingsStore.INSTANCE.setDirty();

      // BAD IMPLEMENTATION: this method is called when the player's menu closes (e.g. when escape is pressed), so this
      // currently re-opens the UI when it shouldn't.
      PacketDistributor.sendToPlayer(serverPlayer, new ShowBuildingScreen(building.getBuildingId()));
   }
}
