package com.uncreated.civilized.core.building.util;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.uncreated.civilized.ui.style.Colors;
import com.uncreated.civilized.core.StoreOperation;
import com.uncreated.civilized.core.building.Building;
import com.uncreated.civilized.core.building.BuildingStore;
import com.uncreated.civilized.core.building.ServerBuildingsStore;
import com.uncreated.civilized.core.settlement.ServerSettlementsStore;
import com.uncreated.civilized.core.settlement.Settlement;
import com.uncreated.civilized.core.villagerinfo.VillagerInfo;
import com.uncreated.civilized.core.villagerinfo.VillagerStore;
import com.uncreated.civilized.networking.packets.CreateNewBuilding;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class BuildingUtil {

   public static List<VillagerInfo> getOccupants(Building building, VillagerStore store) {
      return store.all().stream().filter(v -> v.isOccupantOf(building)).toList();
   }

   public static boolean isBuildingFull(Building building, VillagerStore store) {
      return store.all().stream().filter(v -> v.isOccupantOf(building)).count() == 2;
   }

   public static Optional<Building> findUnoccupiedHome(
         UUID settlementId,
         BuildingStore buildingStore,
         VillagerStore villagerStore) {

      return buildingStore.all()
            .stream()
            .filter(
                  b -> b.getSettlementId().equals(settlementId) && b.getBuildingType().isProperHome()
                        && !isBuildingFull(b, villagerStore))
            .findFirst();
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
