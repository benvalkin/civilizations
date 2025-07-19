package com.uncreated.civilized.core.dialogue.questline.traveler.controller;

import org.jetbrains.annotations.Nullable;

import com.uncreated.civilized.core.dialogue.controller.DialogueController;
import com.uncreated.civilized.core.dialogue.controller.DialogueFlow;
import com.uncreated.civilized.core.dialogue.questline.traveler.JoinSettlementQuest;
import com.uncreated.civilized.entity.CivilizedVillager;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;

public class TravellerDialogueController extends DialogueController {
   @Override
   public @Nullable DialogueFlow getDialogueFlow(CivilizedVillager villager, Player player, InteractionHand hand) {
      return new JoinSettlementQuest();
   }
}
