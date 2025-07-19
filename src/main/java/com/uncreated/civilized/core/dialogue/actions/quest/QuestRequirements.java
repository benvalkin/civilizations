package com.uncreated.civilized.core.dialogue.actions.quest;

import java.util.UUID;

import com.uncreated.civilized.core.dialogue.Dialogue.IDialogueAvailabilityCheck;
import com.uncreated.civilized.core.quest.QuestType;
import com.uncreated.civilized.core.quest.attachments.PlayerQuests;
import com.uncreated.civilized.neoforge.registration.attachments.DataAttachments;

import lombok.Getter;

@Getter
public class QuestRequirements {

   public static IDialogueAvailabilityCheck hasActiveIncompleteQuest(QuestType questType) {
      return context -> {
         PlayerQuests quests = context.getPlayer().getData(DataAttachments.QUESTS);
         UUID vendorId = questType.isVendorSpecific() ? context.getVillager().getInfo().getVillagerId() : null;
         return quests.hasActiveIncompleteQuest(questType, vendorId);
      };
   }

   public static IDialogueAvailabilityCheck hasNeverBeenGivenQuest(QuestType questType) {
      return context -> {
         PlayerQuests quests = context.getPlayer().getData(DataAttachments.QUESTS);
         UUID vendorId = questType.isVendorSpecific() ? context.getVillager().getInfo().getVillagerId() : null;
         return quests.hasNeverCompletedQuest(questType, vendorId);
      };
   }

   public static IDialogueAvailabilityCheck hasFullyCompletedAButNeverBeenGivenB(QuestType A, QuestType B) {
      return context -> {
         PlayerQuests quests = context.getPlayer().getData(DataAttachments.QUESTS);
         UUID vendorIdA = A.isVendorSpecific() ? context.getVillager().getInfo().getVillagerId() : null;
         UUID vendorIdB = B.isVendorSpecific() ? context.getVillager().getInfo().getVillagerId() : null;
         return quests.hasFullyCompletedAButNeverBeenGivenB(A, vendorIdA, B, vendorIdB);
      };
   }

   public static IDialogueAvailabilityCheck isActiveQuestComplete(QuestType questType) {
      return context -> {
         PlayerQuests quests = context.getPlayer().getData(DataAttachments.QUESTS);
         UUID vendorId = questType.isVendorSpecific() ? context.getVillager().getInfo().getVillagerId() : null;
         return quests.hasActiveCompletedQuest(questType, vendorId);
      };
   }

   public static IDialogueAvailabilityCheck isActiveQuestIncomplete(QuestType questType) {
      return context -> {
         PlayerQuests quests = context.getPlayer().getData(DataAttachments.QUESTS);
         UUID vendorId = questType.isVendorSpecific() ? context.getVillager().getInfo().getVillagerId() : null;
         return quests.hasActiveIncompleteQuest(questType, vendorId);
      };
   }
}
