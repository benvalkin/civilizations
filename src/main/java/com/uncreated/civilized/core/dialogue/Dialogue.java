package com.uncreated.civilized.core.dialogue;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import javax.annotation.Nullable;

import com.uncreated.civilized.core.dialogue.context.DialogueContext;

import lombok.Getter;
import net.minecraft.network.chat.Component;

@Getter
public class Dialogue implements IVillageDialogue {
   private @Nullable String key;
   private final Component villagerSpeech;
   private List<ResponseOption> responseOptions;
   private IDialogueAvailabilityCheck availabilityCheck;
   private @Nullable Dialogue fallback;
   private Consumer<DialogueContext> onEnded;
   @Nullable
   private Dialogue nextPage;

   protected Dialogue(Component villagerSpeech) {
      this.villagerSpeech = villagerSpeech;
      this.responseOptions = new ArrayList<>();
      this.availabilityCheck = context -> true;
      this.onEnded = context -> {
      };
   }

   public static Dialogue dialogue(Component villagerSpeech) {
      return new Dialogue(villagerSpeech);
   }

   public Dialogue key(String key) {
      this.key = key;
      return this;
   }

   public Dialogue response(ResponseOption option) {
      responseOptions.add(option);
      return this;
   }

   public Dialogue fallback(Dialogue fallback) {
      this.fallback = fallback;
      return this;
   }

   public Dialogue onEnded(Consumer<DialogueContext> onEnded) {
      this.onEnded = onEnded;
      return this;
   }

   public Dialogue withCloseButton() {
      responseOptions.add(ResponseOption.closeDialogue());
      return this;
   }

   public Dialogue nextPage(Dialogue next) {
      this.nextPage = next;
      return this;
   }

   public Dialogue showIf(IDialogueAvailabilityCheck dialogueAvailabilityCheck) {
      this.availabilityCheck = dialogueAvailabilityCheck;
      return this;
   }

   public boolean hasNextPage() {
      return this.nextPage != null;
   }

   public boolean isAvailableToPlayer(DialogueContext context) {
      return availabilityCheck.isAvailableToPlayer(context);
   }

   public static Dialogue simplePage(Component villagerSpeech) {
      return dialogue(villagerSpeech).response(ResponseOption.nextPage());
   }

   public static Dialogue simplePage(Component villagerSpeech, Component playerSpeech) {
      return dialogue(villagerSpeech).response(ResponseOption.nextPage(playerSpeech));
   }

   public static Dialogue finalPage(Component villagerSpeech) {
      return dialogue(villagerSpeech).response(ResponseOption.closeDialogue());
   }

   public interface IDialogueAvailabilityCheck {
      boolean isAvailableToPlayer(DialogueContext context);
   }
}
