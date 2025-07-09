package com.uncreated.civilized.core.settlement;

import static com.uncreated.civilized.CivilizedMod.CIVILIZED_MOD_ID;

import java.util.List;
import java.util.UUID;

import org.apache.commons.compress.utils.Lists;

import com.uncreated.civilized.core.StoreOperation;
import com.uncreated.civilized.networking.PacketHelper;

import lombok.Builder;
import lombok.Getter;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;

@Getter
@Builder
public class Settlement {

   // The stream decoder reference
   public static Settlement decode(FriendlyByteBuf buffer) {
      return Settlement.builder()
            .settlementId(buffer.readUUID())
            .ownerId(buffer.readUUID())
            .displayName(buffer.readUtf())
            .citizenIds(PacketHelper.readPrefixedList(buffer, b -> b.readUUID()))
            .build();
   }

   // The stream encoder reference
   public void encode(FriendlyByteBuf buffer) {
      buffer.writeUUID(settlementId);
      buffer.writeUUID(ownerId);
      buffer.writeUtf(displayName);
      PacketHelper.writePrefixedList(buffer, citizenIds, (b, i) -> b.writeUUID(i));
   }

   public static final String FIELD_SETTLEMENT_ID = "settlement_id";
   public static final String FIELD_OWNER_ID = "owner_id";
   public static final String FIELD_DISPLAY_NAME = "display_name";
   public static final String FIELD_LIST_CITIZENS = "field_list_citizens";
   public static final String FIELD_LIST_ITEM_CITIZEN_ID = "field_list_item_citizen_id";

   private UUID settlementId;
   private UUID ownerId;
   private String displayName;
   @Builder.Default
   private List<UUID> citizenIds = Lists.newArrayList();

   public void updateInfo(String displayName) {

      this.displayName = displayName;
   }

   public Settlement.Packet toPacket() {
      return new Settlement.Packet(this, StoreOperation.UPDATE);
   }

   public Settlement.Packet toPacket(StoreOperation operation) {
      return new Settlement.Packet(this, operation);
   }

   public void copyFrom(Settlement other) {
      settlementId = other.settlementId;
      ownerId = other.ownerId;
      displayName = other.displayName;
      citizenIds = other.citizenIds; // TECHDEBT: this is sus if we are saving the list reference anywhere
   }

   public void serverTick(ServerLevel level, long gameTime) {

   }

   public String toStringLite() {
      return String.format("{settlementId: %s displayName: %s, pop: %s}", settlementId, displayName, citizenIds.size());
   }

   public record Packet(Settlement settlement, StoreOperation storeOperation) implements CustomPacketPayload {

      public static final CustomPacketPayload.Type<Settlement.Packet> SYNC_TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(CIVILIZED_MOD_ID, "sync_settlement"));

      public static StreamCodec<FriendlyByteBuf, Settlement.Packet> CODEC =
            StreamCodec.ofMember(Settlement.Packet::encode, Settlement.Packet::decode);

      public static Settlement.Packet decode(FriendlyByteBuf buffer) {
         return new Settlement.Packet(Settlement.decode(buffer), buffer.readEnum(StoreOperation.class));
      }

      public void encode(FriendlyByteBuf buffer) {
         settlement.encode(buffer);
         buffer.writeEnum(storeOperation);
      }

      @Override
      public Type<? extends CustomPacketPayload> type() {
         return SYNC_TYPE;
      }
   }
}
