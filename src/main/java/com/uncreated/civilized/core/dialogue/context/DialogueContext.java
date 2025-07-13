package com.uncreated.civilized.core.dialogue.context;

import com.uncreated.civilized.core.dialogue.IVillageDialogue;
import com.uncreated.civilized.entity.CivilizedVillager;

import lombok.Getter;
import net.minecraft.world.entity.player.Player;

@Getter
public class DialogueContext {
   protected final IVillageDialogue dialogue;
   protected final CivilizedVillager villager;
   protected final Player player;

   public DialogueContext(IVillageDialogue dialogue, CivilizedVillager villager, Player player) {
      this.dialogue = dialogue;
      this.villager = villager;
      this.player = player;
   }

   public <T extends DialogueContext> T as() {
      try {
         return (T) this;
      } catch (ClassCastException ex) {
         throw new UnexpectedDialogueContextException(this.getClass());
      }
   }

   public static class UnexpectedDialogueContextException extends RuntimeException {
      public UnexpectedDialogueContextException(Class<? extends DialogueContext> actual) {
         super(
               String.format(
                     "Incorrect type of dialogue context was provided: %s. Make sure that the dialogue screen is passed the right type of dialogue context.",
                     actual));
      }
   }
}
