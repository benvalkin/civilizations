package com.uncreated.civilized.core.building.events;

import com.uncreated.civilized.core.building.ClientBuildingStore;
import com.uncreated.civilized.core.building.ServerBuildingsStore;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

public class BuildingStoreEvents {

   @SubscribeEvent
   public static void playerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
      if (!event.getEntity().level().isClientSide && event.getEntity() instanceof ServerPlayer serverPlayer)
         ServerBuildingsStore.INSTANCE.replicateFullToNewClient(serverPlayer);
      else
         ClientBuildingStore.loadClient(event.getEntity().level());
   }

   @SubscribeEvent
   public static void onServerTick(final ServerTickEvent.Post event) {
      if (ServerBuildingsStore.INSTANCE == null)
         return;

      ServerBuildingsStore.INSTANCE.onServerTick(event.getServer(), event.hasTime());
   }
}
