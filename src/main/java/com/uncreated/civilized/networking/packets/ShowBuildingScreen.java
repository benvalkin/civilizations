package com.uncreated.civilized.networking.packets;

import static com.uncreated.civilized.CivilizedMod.CIVILIZED_MOD_ID;

import java.util.Optional;
import java.util.UUID;

import com.uncreated.civilized.core.building.Building;
import com.uncreated.civilized.core.building.ClientBuildingStore;
import com.uncreated.civilized.core.settlement.ClientSettlementsStore;
import com.uncreated.civilized.core.settlement.Settlement;
import com.uncreated.civilized.ui.menu.building.ABuildingScreen;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ShowBuildingScreen(UUID buildingId) implements CustomPacketPayload {
   public static final CustomPacketPayload.Type<ShowBuildingScreen> TYPE =
         new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(CIVILIZED_MOD_ID, "show_building_screen"));

   public static StreamCodec<FriendlyByteBuf, ShowBuildingScreen> STREAM_CODEC =
         StreamCodec.ofMember(ShowBuildingScreen::encode, ShowBuildingScreen::decode);

   public static ShowBuildingScreen decode(FriendlyByteBuf buffer) {
      return new ShowBuildingScreen(buffer.readUUID());
   }

   public void encode(FriendlyByteBuf buffer) {
      buffer.writeUUID(buildingId);
   }

   @Override
   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }

   public static void clientReceiveShowBuildingScreen(ShowBuildingScreen packet, IPayloadContext context) {

      Optional<Building> building = ClientBuildingStore.INSTANCE.find(packet.buildingId);
      if (building.isEmpty())
         return;

      Settlement settlement = ClientSettlementsStore.INSTANCE.get(building.get().getSettlementId());

      // BuildingMenu buildingMenu =
      // new BuildingMenu(0, context.player().getInventory(), new SimpleContainer(9), settlement, building.get());

      Minecraft.getInstance().setScreen(ABuildingScreen.factory(building.get(), settlement));
   }
}
