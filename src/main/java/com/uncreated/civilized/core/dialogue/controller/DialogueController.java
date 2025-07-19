package com.uncreated.civilized.core.dialogue.controller;

import javax.annotation.Nullable;

import com.uncreated.civilized.core.dialogue.questline.advisor.controller.AdvisorDialogueController;
import com.uncreated.civilized.core.dialogue.questline.traveler.controller.TravellerDialogueController;
import com.uncreated.civilized.core.villagerinfo.VillagerNpcRole;
import com.uncreated.civilized.entity.CivilizedVillager;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;

import java.util.List;

public abstract class DialogueController {

   public DialogueController() {

   }

   public abstract @Nullable DialogueFlow getDialogueFlow(
         CivilizedVillager villager,
         Player player,
         InteractionHand hand);

   public static DialogueController noDialogue() {
      return new DialogueController() {
         @Override
         public @Nullable DialogueFlow getDialogueFlow(CivilizedVillager villager, Player player, InteractionHand hand)  {
            return null;
         }
      };
   }

   public static DialogueController selectDialogueController(CivilizedVillager villager) {
      DialogueController controller;
      List<VillagerNpcRole> roles = villager.getInfo().getNpcRoles();

      if (roles.contains(VillagerNpcRole.ADVISOR)) {
         controller = new AdvisorDialogueController();
      } else if (roles.contains(VillagerNpcRole.TRAVELLER)) {
         controller = new TravellerDialogueController();
      }
      else
         return DialogueController.noDialogue();

      return controller;
   }
}
