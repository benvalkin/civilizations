package com.uncreated.civilized.core.dialogue.quest;

import java.util.UUID;

import com.uncreated.civilized.core.dialogue.Dialogue.IDialogueAvailabilityCheck;
import com.uncreated.civilized.core.quest.QuestType;
import com.uncreated.civilized.core.quest.attachments.PlayerQuests;
import com.uncreated.civilized.neoforge.registration.attachments.DataAttachments;

import lombok.Getter;

@Getter
public class QuestRequirements {

   public static IDialogueAvailabilityCheck hasActiveQuest(QuestType questType) {
      return context -> {
         PlayerQuests quests = context.getPlayer().getData(DataAttachments.QUESTS);
         UUID vendorId = context.getVillager().getInfo().getVillagerId();
         if (questType.isVendorSpecific())
            return quests.hasActiveQuest(questType, vendorId);

         return quests.hasActiveQuest(questType);
      };
   }

   public static IDialogueAvailabilityCheck hasNotStartedQuest(QuestType questType) {
      return context -> {
         PlayerQuests quests = context.getPlayer().getData(DataAttachments.QUESTS);
         UUID vendorId = context.getVillager().getInfo().getVillagerId();
         if (questType.isVendorSpecific())
            return !quests.hasStartedOrCompleted(questType, vendorId);

         return !quests.hasStartedOrCompleted(questType);
      };
   }

   public static IDialogueAvailabilityCheck hasCompletedQuest(QuestType questType) {
      return context -> {
         PlayerQuests quests = context.getPlayer().getData(DataAttachments.QUESTS);
         UUID vendorId = context.getVillager().getInfo().getVillagerId();
         if (questType.isVendorSpecific())
            return quests.hasCompleted(questType, vendorId);

         return quests.hasCompleted(questType);
      };
   }

   public static IDialogueAvailabilityCheck hasNotCompletedQuest(QuestType questType) {
      return context -> {
         PlayerQuests quests = context.getPlayer().getData(DataAttachments.QUESTS);
         UUID vendorId = context.getVillager().getInfo().getVillagerId();
         if (questType.isVendorSpecific())
            return !quests.hasCompleted(questType, vendorId);

         return !quests.hasCompleted(questType);
      };
   }
}
