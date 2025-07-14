package com.uncreated.civilized.core.dialogue;

import java.util.List;

import net.minecraft.network.chat.Component;

public interface IVillageDialogue {
   String getKey();

   Component getVillagerSpeech();

   List<ResponseOption> getResponseOptions();

   Dialogue getNextPage();

   boolean hasNextPage();
}
