package com.uncreated.civilized.item;

import java.util.Optional;

import org.jetbrains.annotations.Nullable;

import com.uncreated.civilized.core.building.Building;
import com.uncreated.civilized.core.building.ServerBuildingsStore;
import com.uncreated.civilized.core.settlement.ServerSettlementsStore;
import com.uncreated.civilized.core.settlement.Settlement;
import com.uncreated.civilized.ui.menu.building.BuildingMenu;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;

public class SettlementMandateItem extends Item {

   public SettlementMandateItem(Properties properties) {
      super(properties);
   }

   public InteractionResult useOn(UseOnContext context) {

      if (context.getLevel().isClientSide || !(context.getPlayer() instanceof ServerPlayer serverPlayer))
         return InteractionResult.PASS;

      BlockPos clicked = context.getClickedPos();
      BlockPos clickedAir = clicked.mutable().move(context.getClickedFace());

      Optional<Building> enclosingBuilding = ServerBuildingsStore.INSTANCE.findEnclosingBuilding(clickedAir);
      if (enclosingBuilding.isPresent()) {

         Settlement settlement = ServerSettlementsStore.INSTANCE.get(enclosingBuilding.get().getSettlementId());

         serverPlayer.openMenu(new MenuProvider() {
            @Override
            public Component getDisplayName() {
               return enclosingBuilding.get().getBuildingType().translationDark().withStyle(ChatFormatting.UNDERLINE);
            }

            @Override
            public @Nullable AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
               return new BuildingMenu(i, inventory, new SimpleContainer(9), settlement, enclosingBuilding.get());
            }

            @Override
            public void writeClientSideData(AbstractContainerMenu menu, RegistryFriendlyByteBuf buffer) {
               buffer.writeUUID(settlement.getSettlementId());
               buffer.writeUUID(enclosingBuilding.get().getBuildingId());
            }
         });

         return InteractionResult.SUCCESS;
      }

      return InteractionResult.SUCCESS;
   }
}
