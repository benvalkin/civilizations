package com.uncreated.civilized.entity.behaviour;

import com.uncreated.civilized.entity.CivilizedVillager;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.behavior.declarative.Trigger;

public class UpdateActivityFromSchedule {
    public static BehaviorControl<CivilizedVillager> create() {
        return BehaviorBuilder.create((p_259429_) -> p_259429_.point((Trigger)(p_382734_, p_382735_, p_382736_) -> {
            p_382735_.getBrain().updateActivityFromSchedule(p_382734_.getDayTime(), p_382734_.getGameTime());
            return true;
        }));
    }
}
