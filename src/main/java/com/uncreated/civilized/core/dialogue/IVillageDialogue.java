package com.uncreated.civilized.core.dialogue;

import java.util.List;
import java.util.function.Consumer;

import com.uncreated.civilized.core.dialogue.context.DialogueContext;

import net.minecraft.network.chat.Component;

public interface IVillageDialogue {
   String getKey();

   Component getVillagerSpeech();

   List<ResponseOption> getResponseOptions();

   Dialogue getNextPage();

   boolean hasNextPage();

   boolean isAvailableToPlayer(DialogueContext context);

   Dialogue getFallback();

   Consumer<DialogueContext> getOnEnded();
}
