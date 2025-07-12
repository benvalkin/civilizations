package com.uncreated.civilized.core.building.events;

import com.uncreated.civilized.core.building.ClientBuildingStore;
import com.uncreated.civilized.core.building.ServerBuildingsStore;

import net.minecraft.client.Minecraft;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;


public class BuildingStoreEvents {

   @SubscribeEvent
   public static void serverPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
      if (!event.getEntity().level().isClientSide && event.getEntity() instanceof ServerPlayer serverPlayer)
         ServerBuildingsStore.INSTANCE.replicateFullToNewClient(serverPlayer);
   }

   @SubscribeEvent
   public static void onServerTick(final ServerTickEvent.Post event) {
      if (ServerBuildingsStore.INSTANCE == null)
         return;

      ServerBuildingsStore.INSTANCE.onServerTick(event.getServer(), event.hasTime());
   }
}
