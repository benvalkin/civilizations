package com.uncreated.civilized.core.dialogue.questline.advisor.controller;

import java.util.UUID;

import javax.annotation.Nullable;

import com.uncreated.civilized.core.dialogue.controller.DialogueController;
import com.uncreated.civilized.core.dialogue.controller.DialogueFlow;
import com.uncreated.civilized.core.dialogue.questline.advisor.AdvisorIntroQuest1;
import com.uncreated.civilized.core.dialogue.questline.advisor.AdvisorIntroQuest2;
import com.uncreated.civilized.core.quest.Quests;
import com.uncreated.civilized.core.quest.attachments.PlayerQuests;
import com.uncreated.civilized.entity.CivilizedVillager;
import com.uncreated.civilized.neoforge.registration.attachments.DataAttachments;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;

public class AdvisorDialogueController extends DialogueController {
   @Override
   public @Nullable DialogueFlow getDialogueFlow(CivilizedVillager villager, Player player, InteractionHand hand) {

      PlayerQuests quests = player.getData(DataAttachments.QUESTS);
      UUID vendorId = villager.getVillagerId();

      if (quests.hasNeverCompletedQuest(Quests.ADVISOR_INTRO_1, vendorId))
         return new AdvisorIntroQuest1();
      else if (quests.hasNeverCompletedQuest(Quests.ADVISOR_INTRO_2_CHECKPOINT, vendorId))
         return new AdvisorIntroQuest2();

      return null;
   }
}
