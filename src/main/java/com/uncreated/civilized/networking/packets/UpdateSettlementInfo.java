package com.uncreated.civilized.networking.packets;

import static com.uncreated.civilized.CivilizedMod.CIVILIZED_MOD_ID;

import java.util.UUID;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record UpdateSettlementInfo(UUID settlementId, String displayName) implements CustomPacketPayload {

   public static final Type<UpdateSettlementInfo> TYPE =
         new Type<>(ResourceLocation.fromNamespaceAndPath(CIVILIZED_MOD_ID, "update_settlement_info"));

   public static StreamCodec<FriendlyByteBuf, UpdateSettlementInfo> CODEC =
         StreamCodec.ofMember(UpdateSettlementInfo::encode, UpdateSettlementInfo::decode);

   public static UpdateSettlementInfo decode(FriendlyByteBuf buffer) {
      return new UpdateSettlementInfo(buffer.readUUID(), buffer.readUtf());
   }

   public void encode(FriendlyByteBuf buffer) {
      buffer.writeUUID(settlementId);
      buffer.writeUtf(displayName);
   }

   @Override
   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }
}
