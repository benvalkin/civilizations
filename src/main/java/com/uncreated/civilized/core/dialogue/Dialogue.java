package com.uncreated.civilized.core.dialogue;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import lombok.Getter;
import net.minecraft.network.chat.Component;

@Getter
public class Dialogue implements IVillageDialogue {
   private @Nullable String key;
   private final Component villagerSpeech;
   private List<ResponseOption> responseOptions;
   @Nullable
   private Dialogue nextPage;

   protected Dialogue(Component villagerSpeech) {
      this.villagerSpeech = villagerSpeech;
      this.responseOptions = new ArrayList<>();
      this.nextPage = null;
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

   public Dialogue close() {
      responseOptions.add(ResponseOption.closeDialogue());
      return this;
   }

   public Dialogue nextPage(Dialogue next) {
      this.nextPage = next;
      return this;
   }

   public boolean hasNextPage() {
      return this.nextPage != null;
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
}
