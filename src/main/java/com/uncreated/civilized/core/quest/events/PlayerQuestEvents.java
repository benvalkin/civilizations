package com.uncreated.civilized.core.quest.events;

import com.uncreated.civilized.CivilizedMod;
import com.uncreated.civilized.core.quest.attachments.PlayerQuests;
import com.uncreated.civilized.neoforge.registration.attachments.DataAttachments;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

@EventBusSubscriber(modid = CivilizedMod.CIVILIZED_MOD_ID)
public class PlayerQuestEvents {

   @SubscribeEvent
   public static void syncQuestsOnLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {

      if (!(event.getEntity() instanceof ServerPlayer serverPlayer))
         return;

      PlayerQuests quests = event.getEntity().getData(DataAttachments.QUESTS);
      quests.syncAllToClient(serverPlayer);
   }
}
