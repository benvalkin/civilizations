package com.uncreated.civilized.networking.packets;

import static com.uncreated.civilized.CivilizedMod.CIVILIZED_MOD_ID;

import java.util.Optional;
import java.util.UUID;

import javax.annotation.Nullable;

import org.apache.commons.lang3.function.TriFunction;

import com.uncreated.civilized.core.building.Building;
import com.uncreated.civilized.core.building.ServerBuildingsStore;
import com.uncreated.civilized.core.settlement.ClientSettlementsStore;
import com.uncreated.civilized.core.settlement.Settlement;
import com.uncreated.civilized.ui.menu.building.item.management.ItemManagementMenu;
import com.uncreated.civilized.ui.menu.building.worksite.cropfarm.items.ChooseCropsMenu;
import com.uncreated.civilized.ui.menu.building.worksite.grove.items.ChooseSaplingsMenu;

import net.minecraft.ChatFormatting;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record RequestBuildingItemManagementScreen(UUID buildingId, int containerSize) implements CustomPacketPayload {
   public static final Type<RequestBuildingItemManagementScreen> TYPE =
         new Type<>(ResourceLocation.fromNamespaceAndPath(CIVILIZED_MOD_ID, "show_modify_items_screen"));

   public static StreamCodec<FriendlyByteBuf, RequestBuildingItemManagementScreen> STREAM_CODEC =
         StreamCodec.ofMember(RequestBuildingItemManagementScreen::encode, RequestBuildingItemManagementScreen::decode);

   public static RequestBuildingItemManagementScreen decode(FriendlyByteBuf buffer) {
      return new RequestBuildingItemManagementScreen(buffer.readUUID(), buffer.readInt());
   }

   public void encode(FriendlyByteBuf buffer) {
      buffer.writeUUID(buildingId);
      buffer.writeInt(containerSize);
   }

   @Override
   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }

   public static void serverReceiveRequestModifyItemsScreen(
         RequestBuildingItemManagementScreen packet,
         IPayloadContext context) {

      Optional<Building> building = ServerBuildingsStore.INSTANCE.find(packet.buildingId);
      if (building.isEmpty())
         return;

      Settlement settlement = ClientSettlementsStore.INSTANCE.get(building.get().getSettlementId());

      TriFunction<Integer, Inventory, Player, ItemManagementMenu> menuSupplier =
            switch (building.get().getBuildingType()) {
            case CROP_FARM -> (
                  i,
                  inventory,
                  player) -> new ChooseCropsMenu(i, inventory, new SimpleContainer(3), settlement, building.get());
            case GROVE -> (
                  i,
                  inventory,
                  player) -> new ChooseSaplingsMenu(i, inventory, new SimpleContainer(1), settlement, building.get());
            default -> throw new IllegalArgumentException();
            };

      context.player().openMenu(new MenuProvider() {
         @Override
         public Component getDisplayName() {
            return building.get().getBuildingType().translationDark().withStyle(ChatFormatting.UNDERLINE);
         }

         @Override
         public @Nullable AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
            return menuSupplier.apply(i, inventory, player);
         }

         @Override
         public void writeClientSideData(AbstractContainerMenu menu, RegistryFriendlyByteBuf buffer) {
            buffer.writeInt(packet.containerSize);
            buffer.writeUUID(settlement.getSettlementId());
            buffer.writeUUID(building.get().getBuildingId());
         }
      });

   }
}
