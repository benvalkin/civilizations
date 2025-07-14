package com.uncreated.civilized.core.dialogue.context;

import com.uncreated.civilized.entity.CivilizedVillager;

import lombok.Getter;
import net.minecraft.world.entity.player.Player;

@Getter
public class DialogueTranslationArgs {
   private final CivilizedVillager villager;
   private final Player player;

   public DialogueTranslationArgs(CivilizedVillager villager, Player player) {
      this.villager = villager;
      this.player = player;
   }
}
