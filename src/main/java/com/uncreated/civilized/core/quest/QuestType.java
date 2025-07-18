package com.uncreated.civilized.core.quest;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.Objects;

@Getter
public class QuestType {

   private final String name;
   private final boolean repeatable;
   private final boolean vendorSpecific;

   private QuestType(String name, boolean repeatable, boolean vendorSpecific) {
      this.name = name;
      this.repeatable = repeatable;
      this.vendorSpecific = vendorSpecific;
   }

   public static QuestProperties properties(String type) {
      return new QuestProperties(type);
   }

   public static class QuestProperties {
      @Getter
      private final String name;
      private boolean repeatable;
      private boolean vendorSpecific;

      private QuestProperties(String name) {
         this.name = name;
         this.repeatable = false;
         this.vendorSpecific = false;
      }

      public QuestProperties repeatable(boolean repeatable) {
         this.repeatable = repeatable;
         return this;
      }

      public QuestProperties vendorSpecific(boolean vendorSpecific) {
         this.vendorSpecific = vendorSpecific;
         return this;
      }

      public QuestType build() {
         return new QuestType(name, repeatable, vendorSpecific);
      }
   }

   @Override
   public boolean equals(Object object) {
      if (object == null || getClass() != object.getClass()) return false;
      QuestType questType = (QuestType) object;
      return Objects.equals(name, questType.name);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(name);
   }

   @Override
   public String toString() {
      return name;
   }
}
