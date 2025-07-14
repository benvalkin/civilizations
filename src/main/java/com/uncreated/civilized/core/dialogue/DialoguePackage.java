package com.uncreated.civilized.core.dialogue;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

public class DialoguePackage {
   private final List<Supplier<IVillageDialogue>> dialogues;
   private final Random random;

   private DialoguePackage() {
      dialogues = new ArrayList<>();
      this.random = new Random();
   }

   public IVillageDialogue chooseRandom() {
      int randomIndex = random.nextInt(0, dialogues.size());
      return dialogues.get(randomIndex).get();
   }

   public static DialoguePackage dialoguePackage() {
      return new DialoguePackage();
   }
   public DialoguePackage add(IVillageDialogue dialogue) {
      dialogues.add(() -> dialogue);
      return this;
   }
}
