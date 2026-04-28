package com.uncreated.civilized.networking.packets;

import static com.uncreated.civilized.CivilizedMod.CIVILIZED_MOD_ID;

import java.util.Optional;
import java.util.UUID;

import com.uncreated.civilized.core.building.Building;
import com.uncreated.civilized.core.building.BuildingType;
import com.uncreated.civilized.core.building.ClientBuildingStore;
import com.uncreated.civilized.core.settlement.ClientSettlementsStore;
import com.uncreated.civilized.core.settlement.Settlement;
import com.uncreated.civilized.ui.context.BuildingScreenContext;
import com.uncreated.civilized.ui.menu.building.ABuildingScreen;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ShowBuildingScreen(UUID buildingId, CompoundTag additionalData) implements CustomPacketPayload {
   public static final CustomPacketPayload.Type<ShowBuildingScreen> TYPE =
         new CustomPacketPayload.Type<>(
               ResourceLocation.fromNamespaceAndPath(CIVILIZED_MOD_ID, "show_building_screen"));

   public static StreamCodec<RegistryFriendlyByteBuf, ShowBuildingScreen> STREAM_CODEC =
         StreamCodec.ofMember(ShowBuildingScreen::encode, ShowBuildingScreen::decode);

   public static ShowBuildingScreen decode(RegistryFriendlyByteBuf buffer) {
      return new ShowBuildingScreen(buffer.readUUID(), buffer.readNbt());
   }

   public void encode(RegistryFriendlyByteBuf buffer) {
      buffer.writeUUID(buildingId);
      buffer.writeNbt(additionalData);
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

      BuildingScreenContext buildingScreenContext =
            new BuildingScreenContext(
                  building.get(),
                  settlement,
                  packet.additionalData(),
                  context.player().registryAccess());

      BuildingType buildingType = building.get().getBuildingType();
      Component heading = buildingType.translationDark().withStyle(ChatFormatting.UNDERLINE);

      ABuildingScreen buildingScreen = buildingType.buildingScreenSupplier().apply(buildingScreenContext, heading);
      Minecraft.getInstance().setScreen(buildingScreen);
   }
}
