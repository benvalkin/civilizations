package com.uncreated.civilized.core.dialogue.actions;

import com.uncreated.civilized.core.dialogue.context.DialogueContext;
import com.uncreated.civilized.entity.CivilizedVillager;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;

public class SoundActions {
   public static void playerVillagerSound(DialogueContext context, SoundEvent soundEvent) {
      CivilizedVillager villager = context.getVillager();
      villager.level().playSound(context.getPlayer(), villager.getOnPos(), soundEvent, SoundSource.NEUTRAL, 1, 1);
   }
}
