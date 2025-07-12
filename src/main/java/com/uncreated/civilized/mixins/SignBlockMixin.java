package com.uncreated.civilized.mixins;

import java.util.Optional;
import java.util.UUID;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.uncreated.civilized.core.building.Building;
import com.uncreated.civilized.core.building.ServerBuildingsStore;
import com.uncreated.civilized.core.settlement.ServerSettlementsStore;
import com.uncreated.civilized.core.settlement.Settlement;
import com.uncreated.civilized.neoforge.registration.attachments.DataAttachments;
import com.uncreated.civilized.ui.menu.building.BuildingMenu;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.SignBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

@Mixin(SignBlock.class)
public abstract class SignBlockMixin extends BaseEntityBlock implements SimpleWaterloggedBlock {

   public SignBlockMixin(Properties properties) {
      super(properties);
   }

   @Inject(method = "useWithoutItem", at = @At("HEAD"), cancellable = true, remap = false)
   public void useWithOutItem(
         BlockState blockState,
         Level level,
         BlockPos pos,
         Player player,
         BlockHitResult hit,
         CallbackInfoReturnable<InteractionResult> cir) {

      if (level.isClientSide)
         return;

      if (!(level.getBlockEntity(pos) instanceof SignBlockEntity signEntity))
         return;

      UUID buildingId = signEntity.getData(DataAttachments.LINKED_BUILDING).getBuildingId();
      if (buildingId == null)
         return;

      Optional<Building> building = ServerBuildingsStore.INSTANCE.find(buildingId);
      if (building.isEmpty())
         return;

      Settlement settlement = ServerSettlementsStore.INSTANCE.get(building.get().getSettlementId());

      player.openMenu(new MenuProvider() {
         @Override
         public Component getDisplayName() {
            return building.get().getBuildingType().translationDark().withStyle(ChatFormatting.UNDERLINE);
         }

         @Override
         public @Nullable AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
            return new BuildingMenu(i, inventory, new SimpleContainer(9), settlement, building.get());
         }

         @Override
         public void writeClientSideData(AbstractContainerMenu menu, RegistryFriendlyByteBuf buffer) {
            buffer.writeUUID(settlement.getSettlementId());
            buffer.writeUUID(building.get().getBuildingId());
         }
      });

      cir.setReturnValue(InteractionResult.SUCCESS);
      cir.cancel();
   }
}
