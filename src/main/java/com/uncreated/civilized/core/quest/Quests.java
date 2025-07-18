package com.uncreated.civilized.core.quest;

import java.util.HashMap;

public class Quests {

   private static final HashMap<String, QuestType> registeredQuestTypes = new HashMap<>();

   public static QuestType registerQuest(QuestType.QuestProperties properties) {
      if (registeredQuestTypes.containsKey(properties.getName()))
         throw new UnsupportedOperationException("Quest already registered with ID: " + properties.getName());

      QuestType result = properties.build();
      registeredQuestTypes.put(result.getName(), result);
      return result;
   }

   public static QuestType get(String type) {
      QuestType questType = registeredQuestTypes.get(type);
      if (questType == null)
         throw new IllegalArgumentException(type + " is not a registered quest type.");

      return questType;
   }

   public static final QuestType ADVISOR_MEAL =
         registerQuest(QuestType.properties("advisor_meal").repeatable(true).vendorSpecific(true));
}
