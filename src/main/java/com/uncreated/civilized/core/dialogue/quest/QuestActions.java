package com.uncreated.civilized.core.dialogue.quest;

import java.util.UUID;

import com.uncreated.civilized.core.dialogue.ResponseOption;
import com.uncreated.civilized.core.dialogue.context.DialogueContext;
import com.uncreated.civilized.core.quest.QuestType;
import com.uncreated.civilized.neoforge.registration.attachments.DataAttachments;

import lombok.Getter;
import net.minecraft.world.entity.player.Player;

@Getter
public class QuestActions {

   public static ResponseOption.IOnResponseSelectedAction startQuest(QuestType questType) {
      return startQuest(questType, ResponseOption.SelectedAction.GO_NEXT);
   }

   public static ResponseOption.IOnResponseSelectedAction startQuest(
         QuestType questType,
         ResponseOption.SelectedAction selectAction) {
      return context -> {
         Player player = context.getDialogueContext().getPlayer();
         UUID vendorId = context.getDialogueContext().getVillager().getVillagerId();
         if (questType.isVendorSpecific())
            player.getData(DataAttachments.QUESTS).tryStartQuest(questType, vendorId);
         else
            player.getData(DataAttachments.QUESTS).tryStartQuest(questType);
         return selectAction;
      };
   }

   public static void completeQuest(DialogueContext context, QuestType questType) {
      Player player = context.getPlayer();
      UUID vendorId = context.getVillager().getVillagerId();
      if (questType.isVendorSpecific())
         player.getData(DataAttachments.QUESTS).completeQuest(questType, vendorId);
      else
         player.getData(DataAttachments.QUESTS).completeQuest(questType);
   }

   public static ResponseOption.IOnResponseSelectedAction completeQuest(
         QuestType questType,
         ResponseOption.SelectedAction selectAction) {
      return context -> {
         completeQuest(context.getDialogueContext(), questType);
         return selectAction;
      };
   }

   public static void endQuest(DialogueContext context, QuestType questType) {
      Player player = context.getPlayer();
      UUID vendorId = context.getVillager().getVillagerId();
      if (questType.isVendorSpecific())
         player.getData(DataAttachments.QUESTS).endQuest(questType, vendorId);
      else
         player.getData(DataAttachments.QUESTS).endQuest(questType);
   }

   public static ResponseOption.IOnResponseSelectedAction endQuest(
           QuestType questType,
           ResponseOption.SelectedAction selectAction) {
      return context -> {
         endQuest(context.getDialogueContext(), questType);
         return selectAction;
      };
   }
}
