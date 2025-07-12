package com.uncreated.civilized.networking.packets;

import static com.uncreated.civilized.CivilizedMod.CIVILIZED_MOD_ID;

import com.uncreated.civilized.core.StoreOperation;
import com.uncreated.civilized.core.building.Building;
import com.uncreated.civilized.core.building.BuildingType;
import com.uncreated.civilized.core.building.ServerBuildingsStore;
import com.uncreated.civilized.core.building.bounds.BuildingBounds;

import com.uncreated.civilized.core.settlement.ServerSettlementsStore;
import com.uncreated.civilized.core.settlement.Settlement;
import com.uncreated.civilized.ui.style.Colors;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.Optional;

/**
 * Dedicated client packet to nicely create buildings on the server.
 */
public record CreateNewBuilding(BuildingType buildingType, BuildingBounds buildingBounds) implements CustomPacketPayload {

   public static final CustomPacketPayload.Type<CreateNewBuilding> TYPE =
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
      return TYPE;
   }

   public static void serverReceiveCreateNewBuilding(CreateNewBuilding createNewBuilding, IPayloadContext context) {

      ServerPlayer placer = (ServerPlayer) context.player();
      Optional<Settlement> settlement = ServerSettlementsStore.INSTANCE.findFromOwner(placer.getUUID());
      if (settlement.isEmpty()) {
         settlement = Optional.of(ServerSettlementsStore.INSTANCE.createNew(placer.getUUID()));
         ServerSettlementsStore.INSTANCE.setDirty();
         ServerSettlementsStore.INSTANCE.replicateChange(settlement.get(), StoreOperation.ADD_OR_OVERWRITE);
      }

      Building building =
              ServerBuildingsStore.INSTANCE.createNew(
                      settlement.get().getSettlementId(),
                      placer.getUUID(),
                      createNewBuilding.buildingType(),
                      createNewBuilding.buildingBounds());
      ServerBuildingsStore.INSTANCE.setDirty();
      ServerBuildingsStore.INSTANCE.replicateChange(building, StoreOperation.ADD_OR_OVERWRITE);

      placer.displayClientMessage(
              Component
                      .translatable(
                              "message.building.placement.validation.success",
                              building.getBuildingType().translation())
                      .withColor(Colors.VALIDATION_SUCCESS),
              false);
   }
}
