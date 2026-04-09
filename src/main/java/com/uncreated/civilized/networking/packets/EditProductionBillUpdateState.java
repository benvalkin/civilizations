package com.uncreated.civilized.networking.packets;

import static com.uncreated.civilized.CivilizedMod.CIVILIZED_MOD_ID;

import com.uncreated.civilized.core.building.production.bills.strategy.ProductionStrategyType;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record EditProductionBillUpdateState(ProductionStrategyType desiredProductionStrategyType,
      int desiredProductionAmount) implements CustomPacketPayload {

   public static final Type<EditProductionBillUpdateState> TYPE =
         new Type<>(ResourceLocation.fromNamespaceAndPath(CIVILIZED_MOD_ID, "update_settlement_info"));

   public static StreamCodec<FriendlyByteBuf, EditProductionBillUpdateState> CODEC =
         StreamCodec.ofMember(EditProductionBillUpdateState::encode, EditProductionBillUpdateState::decode);

   public static EditProductionBillUpdateState decode(FriendlyByteBuf buffer) {
      return new EditProductionBillUpdateState(buffer.readEnum(ProductionStrategyType.class), buffer.readInt());
   }

   public void encode(FriendlyByteBuf buffer) {
      buffer.writeEnum(desiredProductionStrategyType);
      buffer.writeInt(desiredProductionAmount);
   }

   @Override
   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }
}
