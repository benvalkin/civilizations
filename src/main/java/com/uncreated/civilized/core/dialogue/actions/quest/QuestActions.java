package com.uncreated.civilized.core.dialogue.actions.quest;

import java.util.UUID;

import com.uncreated.civilized.core.dialogue.ResponseOption;
import com.uncreated.civilized.core.dialogue.context.DialogueContext;
import com.uncreated.civilized.core.quest.QuestType;
import com.uncreated.civilized.core.quest.attachments.PlayerQuests;
import com.uncreated.civilized.neoforge.registration.attachments.DataAttachments;

import lombok.Getter;

@Getter
public class QuestActions {

   public static ResponseOption.IOnResponseSelectedAction startQuest(QuestType questType) {
      return startQuest(questType, ResponseOption.SelectedAction.GO_NEXT);
   }

   public static ResponseOption.IOnResponseSelectedAction startQuest(
         QuestType questType,
         ResponseOption.SelectedAction selectAction) {
      return context -> {
         PlayerQuests quests = context.getDialogueContext().getPlayer().getData(DataAttachments.QUESTS);
         UUID vendorId =
               questType.isVendorSpecific() ? context.getDialogueContext().getVillager().getInfo().getVillagerId()
                     : null;
         quests.tryStartQuest(questType, vendorId);
         return selectAction;
      };
   }

   public static void completeQuest(DialogueContext context, QuestType questType) {
      PlayerQuests quests = context.getPlayer().getData(DataAttachments.QUESTS);
      UUID vendorId = questType.isVendorSpecific() ? context.getVillager().getInfo().getVillagerId() : null;
      quests.completeQuest(questType, vendorId);
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
      PlayerQuests quests = context.getPlayer().getData(DataAttachments.QUESTS);
      UUID vendorId = questType.isVendorSpecific() ? context.getVillager().getInfo().getVillagerId() : null;
      quests.endQuest(questType, vendorId);
   }

   public static ResponseOption.IOnResponseSelectedAction endQuest(
         QuestType questType,
         ResponseOption.SelectedAction selectAction) {
      return context -> {
         endQuest(context.getDialogueContext(), questType);
         return selectAction;
      };
   }

   public static void completeCheckpointQuest(DialogueContext context, QuestType questType) {
      PlayerQuests quests = context.getPlayer().getData(DataAttachments.QUESTS);
      UUID vendorId = questType.isVendorSpecific() ? context.getVillager().getInfo().getVillagerId() : null;
      quests.completeCheckpointQuest(questType, vendorId);
   }
}
