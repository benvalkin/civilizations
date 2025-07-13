package com.uncreated.civilized.core.dialogue;

import java.util.ArrayList;
import java.util.List;

import lombok.Builder;
import lombok.Getter;
import net.minecraft.network.chat.Component;

@Getter
@Builder
public class Dialogue implements IVillageDialogue {
   private final String key;
   private Component villagerSpeech;
   private List<ResponseOption> responseOptions;

   public static DialogueBuilder create(String key) {
      return new DialogueBuilder().key(key).responseOptions(new ArrayList<>());
   }

   public static class DialogueBuilder {

      private List<ResponseOption> responseOptions = new ArrayList<>();

      public DialogueBuilder response(ResponseOption option) {
         responseOptions.add(option);
         return this;
      }
   }

   public static Dialogue simplePage(Component villagerSpeech) {
      return create("simple_page").villagerSpeech(villagerSpeech).response(ResponseOption.nextPage()).build();
   }
   public static Dialogue simplePage(Component villagerSpeech, Component playerSpeech) {
      return create("simple_page").villagerSpeech(villagerSpeech).response(ResponseOption.nextPage(playerSpeech)).build();
   }
}
