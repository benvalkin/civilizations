package com.uncreated.civilized.networking;

import com.uncreated.civilized.core.building.Building;
import com.uncreated.civilized.core.building.ClientBuildingStore;
import com.uncreated.civilized.core.building.ServerBuildingsStore;
import com.uncreated.civilized.core.building.util.BuildingUtil;
import com.uncreated.civilized.core.settlement.ClientSettlementsStore;
import com.uncreated.civilized.core.settlement.ServerSettlementsStore;
import com.uncreated.civilized.core.settlement.Settlement;
import com.uncreated.civilized.core.villagerinfo.ClientVillagerStore;
import com.uncreated.civilized.core.villagerinfo.ServerVillagerStore;
import com.uncreated.civilized.core.villagerinfo.VillagerInfo;

import com.uncreated.civilized.networking.packets.CreateNewBuilding;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.DirectionalPayloadHandler;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class PacketRegistry {
   @SubscribeEvent
   public static void register(final RegisterPayloadHandlersEvent event) {
      // Sets the current network version
      final PayloadRegistrar registrar = event.registrar("1");
      // registrar.executesOn(HandlerThread.NETWORK); // All subsequent payloads will register on the network thread

      registrar.playBidirectional(
            Building.Packet.SYNC_TYPE,
            Building.Packet.CODEC,
            new DirectionalPayloadHandler<>(
                  ClientBuildingStore::receiveSyncFromServer,
                  ServerBuildingsStore::receiveSyncFromClient));

      registrar.playBidirectional(
            Settlement.Packet.SYNC_TYPE,
            Settlement.Packet.CODEC,
            new DirectionalPayloadHandler<>(
                  ClientSettlementsStore::receiveSyncFromServer,
                  ServerSettlementsStore::receiveSyncFromClient));

      registrar.playBidirectional(
            VillagerInfo.Packet.SYNC_TYPE,
            VillagerInfo.Packet.CODEC,
            new DirectionalPayloadHandler<>(
                  ClientVillagerStore::receiveSyncFromServer,
                  ServerVillagerStore::receiveSyncFromClient));

      registrar.playToServer(
              CreateNewBuilding.SYNC_TYPE,
              CreateNewBuilding.CODEC,
              BuildingUtil::serverReceiveCreateNewBuilding);
   }
}
