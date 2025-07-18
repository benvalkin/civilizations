package com.uncreated.civilized.core.dialogue;

import javax.annotation.Nullable;

import lombok.Getter;

@Getter
public class DialoguePath {
   private Dialogue.IDialogueAvailabilityCheck availabilityCheck;
   private Dialogue dialogue;

   public DialoguePath(Dialogue.IDialogueAvailabilityCheck availabilityCheck, Dialogue dialogue) {
      this.availabilityCheck = availabilityCheck;
      this.dialogue = dialogue;
   }

   public static DialoguePath Case(Dialogue.IDialogueAvailabilityCheck shouldChoose, Dialogue dialogue) {
      return new DialoguePath(shouldChoose, dialogue);
   }

   public static DialoguePath defaultCase(Dialogue dialogue) {
      return new DialoguePath(context -> true, dialogue);
   }

   public static @Nullable Dialogue Switch(DialoguePath... paths) {

      @Nullable
      Dialogue first = paths.length > 0 ? paths[0].getDialogue() : null;

      for (int i = 0; i < paths.length; i++) {
         DialoguePath current = paths[i];

         int nextIndex = i + 1;
         @Nullable
         DialoguePath next = nextIndex < paths.length ? paths[nextIndex] : null;

         current.getDialogue().showIf(current.getAvailabilityCheck());

         if (next != null)
            current.getDialogue().fallback(next.getDialogue());
      }

      return first;
   }
}
