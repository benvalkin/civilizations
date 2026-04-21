package com.uncreated.civilized.entity.behaviour;

import com.uncreated.civilized.entity.CivilizedVillager;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.behavior.declarative.Trigger;

public class UpdateActivityFromSchedule {
    public static BehaviorControl<CivilizedVillager> create() {
        return BehaviorBuilder.create((villagerInstance) -> villagerInstance.point((Trigger)(serverLevel, entity, currentTicks) -> {
            entity.getBrain().updateActivityFromSchedule(serverLevel.getDayTime(), serverLevel.getGameTime());
            return true;
        }));
    }
}
