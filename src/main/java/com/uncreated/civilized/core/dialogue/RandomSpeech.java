package com.uncreated.civilized.core.dialogue;

import java.util.Random;

import net.minecraft.network.chat.Component;

public class RandomSpeech {
   public static Component oneOf(Component... speechOptions) {
      int randomIndex = new Random().nextInt(0, speechOptions.length);
      return speechOptions[randomIndex];
   }

   public static Component randomPlayerGreeting() {
      return oneOf(
              Component.translatable("villager.dialogue.response.misc.greet.1"),
              Component.translatable("villager.dialogue.response.misc.greet.2"),
              Component.translatable("villager.dialogue.response.misc.greet.3")
      );
   }

}
