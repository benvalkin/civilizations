package com.example.examplemod.entities.goals;

import com.example.examplemod.entities.CivilizedVillager;
import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class MoveToPointGoal extends Goal {

    private final CivilizedVillager villager;

    public MoveToPointGoal(CivilizedVillager villager) {
        this.villager = villager;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        LogUtils.getLogger().info("Civie can use move target goal: {}", this.villager.getMoveTarget() != null);
        return this.villager.getMoveTarget() != null;
    }

    @Override
    public boolean canContinueToUse() {
        return !this.villager.getNavigation().isDone();
    }

    @Override
    public void start() {

        if (this.villager.getMoveTarget() == null)
            return;

        this.villager.getNavigation().moveTo(villager.getMoveTarget().getX(), villager.getMoveTarget().getY(), villager.getMoveTarget().getZ(), 0.5f);
    }

    @Override
    public void stop() {
        this.villager.getNavigation().stop();
        this.villager.clearMoveTarget();
    }

    @Override
    public void tick() {
        BlockPos blockPos = this.villager.getMoveTarget();
        if (blockPos != null) {
            this.villager.getNavigation().moveTo(blockPos.getX(), blockPos.getY(), blockPos.getZ(), 0.5);
        }
    }
}
