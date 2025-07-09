package com.uncreated.civilized.networking.packets;

import static com.uncreated.civilized.CivilizedMod.CIVILIZED_MOD_ID;

import com.uncreated.civilized.core.building.BuildingType;
import com.uncreated.civilized.core.building.bounds.BuildingBounds;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/**
 * Dedicated client packet to nicely create buildings on the server.
 */
public record CreateNewBuilding(BuildingType buildingType, BuildingBounds buildingBounds) implements CustomPacketPayload {

   public static final CustomPacketPayload.Type<CreateNewBuilding> SYNC_TYPE =
         new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(CIVILIZED_MOD_ID, "create_new_building"));

   public static StreamCodec<FriendlyByteBuf, CreateNewBuilding> CODEC =
         StreamCodec.ofMember(CreateNewBuilding::encode, CreateNewBuilding::decode);

   public static CreateNewBuilding decode(FriendlyByteBuf buffer) {
      return new CreateNewBuilding(buffer.readEnum(BuildingType.class), BuildingBounds.decode(buffer));
   }

   public void encode(FriendlyByteBuf buffer) {
      buffer.writeEnum(buildingType);
      buildingBounds.encode(buffer);
   }

   @Override
   public Type<? extends CustomPacketPayload> type() {
      return SYNC_TYPE;
   }
}
