package com.uncreated.civilized.core.settlement.events;

import com.uncreated.civilized.core.settlement.ServerSettlementsStore;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

public class SettlementStoreEvents {

   @SubscribeEvent
   public static void playerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
      if (!event.getEntity().level().isClientSide && event.getEntity() instanceof ServerPlayer serverPlayer)
         ServerSettlementsStore.INSTANCE.replicateFullToNewClient(serverPlayer);
   }

   @SubscribeEvent
   public static void onServerTick(final ServerTickEvent.Post event) {
      if (ServerSettlementsStore.INSTANCE == null)
         return;

      ServerSettlementsStore.INSTANCE.onServerTick(event.getServer(), event.hasTime());
   }
}
