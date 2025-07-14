package com.uncreated.civilized.core.dialogue.traveler.quest;

import com.uncreated.civilized.core.building.Building;
import com.uncreated.civilized.core.dialogue.IVillageDialogue;
import com.uncreated.civilized.core.dialogue.context.DialogueContext;
import com.uncreated.civilized.core.settlement.Settlement;
import com.uncreated.civilized.entity.CivilizedVillager;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.world.entity.player.Player;

@Getter
@Setter
public class JoinSettlementContext extends DialogueContext {

    private Settlement settlement;
    private Building unoccupiedBuilding;

    public JoinSettlementContext(CivilizedVillager villager, Player player) {
        super(villager, player);
    }
}
