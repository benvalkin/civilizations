package com.uncreated.civilized.core.quest;

import java.util.Objects;

import lombok.Getter;

@Getter
public class QuestType {

   private final String name;
   private final boolean repeatable;
   private final boolean vendorSpecific;
   private final boolean isCheckpoint;

   private QuestType(String name, boolean repeatable, boolean vendorSpecific, boolean isCheckpoint) {
      this.name = name;
      this.repeatable = repeatable;
      this.vendorSpecific = vendorSpecific;
      this.isCheckpoint = isCheckpoint;
   }

   public static QuestProperties properties(String type) {
      return new QuestProperties(type);
   }

   public static class QuestProperties {
      @Getter
      private final String name;
      private boolean repeatable;
      private boolean vendorSpecific;
      private boolean isCheckpoint;

      private QuestProperties(String name) {
         this.name = name;
         this.repeatable = false;
         this.vendorSpecific = true;
         this.isCheckpoint = false;
      }

      public QuestProperties repeatable(boolean repeatable) {
         this.repeatable = repeatable;
         return this;
      }

      public QuestProperties vendorSpecific(boolean vendorSpecific) {
         this.vendorSpecific = vendorSpecific;
         return this;
      }

      public QuestProperties isCheckpoint(boolean isCheckpoint) {
         this.isCheckpoint = isCheckpoint;
         return this;
      }

      public QuestType build() {
         return new QuestType(name, repeatable, vendorSpecific, isCheckpoint);
      }
   }

   @Override
   public boolean equals(Object object) {
      if (object == null || getClass() != object.getClass())
         return false;
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
