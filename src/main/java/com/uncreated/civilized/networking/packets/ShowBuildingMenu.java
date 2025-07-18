package com.uncreated.civilized.networking.packets;

import static com.uncreated.civilized.CivilizedMod.CIVILIZED_MOD_ID;

import java.util.Optional;
import java.util.UUID;

import com.uncreated.civilized.core.building.Building;
import com.uncreated.civilized.core.building.ServerBuildingsStore;
import com.uncreated.civilized.core.settlement.ServerSettlementsStore;
import com.uncreated.civilized.core.settlement.Settlement;
import com.uncreated.civilized.ui.menu.building.BuildingMenu;
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
import org.jetbrains.annotations.Nullable;

public record ShowBuildingMenu(UUID buildingId) implements CustomPacketPayload {
   public static final CustomPacketPayload.Type<ShowBuildingMenu> TYPE =
         new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(CIVILIZED_MOD_ID, "show_building_menu"));

   public static StreamCodec<FriendlyByteBuf, ShowBuildingMenu> STREAM_CODEC =
         StreamCodec.ofMember(ShowBuildingMenu::encode, ShowBuildingMenu::decode);

   public static ShowBuildingMenu decode(FriendlyByteBuf buffer) {
        return new ShowBuildingMenu(buffer.readUUID());
    }

   public void encode(FriendlyByteBuf buffer) {
        buffer.writeUUID(buildingId);
    }

   @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void serverReceiveShowBuildingMenu(ShowBuildingMenu packet, IPayloadContext context) {

        Optional<Building> building = ServerBuildingsStore.INSTANCE.find(packet.buildingId);
        if (building.isEmpty())
            return;

        Settlement settlement = ServerSettlementsStore.INSTANCE.get(building.get().getSettlementId());

        context.player().openMenu(new MenuProvider() {
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

    }
}
