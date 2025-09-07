package com.uncreated.civilized.core.building;

import static com.uncreated.civilized.CivilizedMod.CIVILIZED_MOD_ID;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import org.apache.commons.compress.utils.Lists;

import com.uncreated.civilized.core.StoreOperation;
import com.uncreated.civilized.core.building.behaviour.BuildingBehaviour;
import com.uncreated.civilized.core.building.bounds.BuildingBounds;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.SignBlockEntity;

@Getter
@Builder(builderMethodName = "builder")
@ToString
public class Building {

   public static StreamCodec<RegistryFriendlyByteBuf, Building> CODEC =
         StreamCodec.ofMember(Building::encode, Building::decode);

   public static Building decode(RegistryFriendlyByteBuf buffer) {
      Building result =
            Building.builder()
                  .buildingId(buffer.readUUID())
                  .settlementId(buffer.readUUID())
                  .placerId(buffer.readUUID())
                  .buildingType(buffer.readEnum(BuildingType.class))
                  .bounds(BuildingBounds.decode(buffer))
                  .occupantIds(buffer.readCollection(ArrayList::new, b -> b.readUUID()))
                  .registryAccess(buffer.registryAccess())
                  .build();

      result.behaviour.applyNbt(buffer.readNbt());
      return result;
   }

   // The stream encoder reference
   public void encode(RegistryFriendlyByteBuf buffer) {
      buffer.writeUUID(buildingId);
      buffer.writeUUID(settlementId);
      buffer.writeUUID(placerId);
      buffer.writeEnum(buildingType);
      bounds.encode(buffer);
      buffer.writeCollection(occupantIds, (buf, o) -> buf.writeUUID(o));
      buffer.writeNbt(behaviour.toNbt());
   }

   public static final String FIELD_BUILDING_ID = "instance_uuid";
   public static final String FIELD_SETTLEMENT_ID = "settlement_id";
   public static final String FIELD_PLACER_ID = "placer_id";
   public static final String FIELD_BUILDING_TYPE = "building_type";
   public static final String FIELD_LIST_OCCUPANTS = "field_list_occupants";
   public static final String FIELD_LIST_ITEM_OCCUPANT_ID = "field_list_item_occupant_id";
   public static final String FIELD_CENTER_POS = "center_pos";
   public static final String FIELD_LOWER_CORNER_POS = "lower_corner_pos";
   public static final String FIELD_UPPER_CORNER_POS = "upper_corner_pos";
   public static final String FIELD_BEHAVIOUR_DATA = "behaviour_data";

   private UUID buildingId;
   private UUID settlementId;
   private UUID placerId;
   private BuildingType buildingType;
   private BuildingBounds bounds;
   @Builder.Default
   private List<UUID> occupantIds = Lists.newArrayList();
   private BuildingBehaviour behaviour;
   @Setter
   private @Nullable BlockPos primarySignPos;
   private HolderLookup.Provider registryAccess;

   public Packet toPacket() {
      return new Packet(this, StoreOperation.UPDATE);
   }

   public Packet toPacket(StoreOperation operation) {
      return new Packet(this, operation);
   }

   public void copyFrom(Building other) {
      buildingId = other.buildingId;
      settlementId = other.settlementId;
      placerId = other.placerId;
      buildingType = other.buildingType;
      bounds = other.bounds;
      occupantIds = other.occupantIds; // BAD IMPLEMENTATION: this is sus if we are saving the list reference anywhere
      behaviour.applyNbt(other.behaviour.toNbt());
   }

   public BlockPos getBlockPos() {
      return bounds.getCenter();
   }

   public @Nullable SignBlockEntity getPrimarySign(Level level) {
      if (primarySignPos == null)
         return null;

      if (level.getBlockEntity(primarySignPos) instanceof SignBlockEntity signBlockEntity)
         return signBlockEntity;

      return null;
   }

   public String toStringLite() {
      return String.format(
            "{buildingType: %s buildingId: %s blockPos: %s occupants: %s}",
            buildingType,
            buildingId.toString().substring(0, 6),
            getBlockPos(),
            occupantIds.size());
   }

   // custom builder to set required fields so that the user doesn't have to
   public static BuildingBuilder builder() {
      return new CustomBuildingBuilder();
   }

   public static class CustomBuildingBuilder extends BuildingBuilder {

      @Override
      public Building build() {
         var building = super.build();
         if (building.behaviour == null) {
            building.behaviour = BuildingBehaviour.create(building);
         }

         // TECHDEBT: someone could still try to use builder#behaviour() to set the behaviour, and this will override
         // their change and leave them confused.
         return building;
      }
   }

   public record Packet(Building building, StoreOperation storeOperation) implements CustomPacketPayload {

      public static final CustomPacketPayload.Type<Packet> SYNC_TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(CIVILIZED_MOD_ID, "sync_building"));

      public static StreamCodec<RegistryFriendlyByteBuf, Packet> STREAM_CODEC =
            StreamCodec.ofMember(Packet::encode, Packet::decode);

      public static Packet decode(RegistryFriendlyByteBuf buffer) {
         return new Packet(Building.decode(buffer), buffer.readEnum(StoreOperation.class));
      }

      public void encode(RegistryFriendlyByteBuf buffer) {
         building.encode(buffer);
         buffer.writeEnum(storeOperation);
      }

      @Override
      public Type<? extends CustomPacketPayload> type() {
         return SYNC_TYPE;
      }
   }
}
