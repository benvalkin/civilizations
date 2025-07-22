package com.uncreated.civilized.core.dialogue.controller;

import java.util.List;
import java.util.Random;

import com.uncreated.civilized.core.dialogue.Dialogue;
import com.uncreated.civilized.core.dialogue.IVillageDialogue;
import com.uncreated.civilized.core.dialogue.context.DialogueContext;
import com.uncreated.civilized.core.quest.attachments.PlayerQuests;
import com.uncreated.civilized.entity.CivilizedVillager;
import com.uncreated.civilized.neoforge.registration.attachments.DataAttachments;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;

public abstract class DialogueFlow {

   public DialogueContext buildDialogueContext(CivilizedVillager villager, Player player, InteractionHand hand) {
      return new DialogueContext(villager, player);
   }

   public final IVillageDialogue getOpeningDialogue(DialogueContext context) {
      return getOpeningDialogue(
            context,
            context.getVillager(),
            context.getPlayer(),
            context.getPlayer().getData(DataAttachments.QUESTS));
   }

   protected abstract IVillageDialogue getOpeningDialogue(
         DialogueContext context,
         CivilizedVillager villager,
         Player player,
         PlayerQuests playerQuests);
}
