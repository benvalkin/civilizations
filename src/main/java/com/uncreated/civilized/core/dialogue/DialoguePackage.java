package com.uncreated.civilized.core.dialogue;

import com.mojang.datafixers.util.Pair;
import com.uncreated.civilized.core.dialogue.context.DialogueContext;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

public class DialoguePackage {
   private final List<Pair<Dialogue.IDialogueAvailabilityCheck, Supplier<IVillageDialogue>>> dialogueStarts;
   private final Random random;

   private DialoguePackage() {
      dialogueStarts = new ArrayList<>();
      this.random = new Random();
   }

   public @Nullable IVillageDialogue chooseFirstAvailable(DialogueContext context) {
      for (Pair<Dialogue.IDialogueAvailabilityCheck, Supplier<IVillageDialogue>> dialogueStart : dialogueStarts) {
         if (dialogueStart.getFirst().isAvailableToPlayer(context))
            return dialogueStart.getSecond().get();
      }
      return null;
   }

   public IVillageDialogue chooseRandom() {
      int randomIndex = random.nextInt(0, dialogueStarts.size());
      return dialogueStarts.get(randomIndex).getSecond().get();
   }

   public static DialoguePackage dialoguePackage() {
      return new DialoguePackage();
   }
   public DialoguePackage add(IVillageDialogue dialogue) {
      dialogueStarts.add(Pair.of(context -> true, () -> dialogue));
      return this;
   }
   public DialoguePackage addConditional(Dialogue.IDialogueAvailabilityCheck requirement, IVillageDialogue dialogue) {
      dialogueStarts.add(Pair.of(requirement, () -> dialogue));
      return this;
   }
}
