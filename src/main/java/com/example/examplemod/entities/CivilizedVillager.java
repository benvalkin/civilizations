package com.example.examplemod.entities;

import com.example.examplemod.entities.goals.MoveToPointGoal;
import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class CivilizedVillager extends AgeableMob {

    public CivilizedVillager(EntityType<? extends AgeableMob> entityType, Level level) {
        super(entityType, level);
    }

    public static final EntityDataAccessor<BlockPos> MOVE_TARGET =
            SynchedEntityData.defineId(
                    // The class of the entity.
                    CivilizedVillager.class,
                    // The entity data accessor type.
                    EntityDataSerializers.BLOCK_POS
            );

    public static final EntityDataAccessor<Boolean> HAS_MOVE_TARGET =
            SynchedEntityData.defineId(
                    // The class of the entity.
                    CivilizedVillager.class,
                    // The entity data accessor type.
                    EntityDataSerializers.BOOLEAN
            );

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(HAS_MOVE_TARGET, false);
        builder.define(MOVE_TARGET, BlockPos.ZERO);
    }

    @Override
    public @Nullable AgeableMob getBreedOffspring(ServerLevel serverLevel, AgeableMob ageableMob) {
        return null;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new AvoidEntityGoal<Zombie>(this, Zombie.class, 8.0f, 0.5, 0.5));
        this.goalSelector.addGoal(1, new AvoidEntityGoal<Evoker>(this, Evoker.class, 12.0f, 0.5, 0.5));
        this.goalSelector.addGoal(1, new AvoidEntityGoal<Vindicator>(this, Vindicator.class, 8.0f, 0.5, 0.5));
        this.goalSelector.addGoal(1, new AvoidEntityGoal<Vex>(this, Vex.class, 8.0f, 0.5, 0.5));
        this.goalSelector.addGoal(1, new AvoidEntityGoal<Pillager>(this, Pillager.class, 15.0f, 0.5, 0.5));
        this.goalSelector.addGoal(1, new AvoidEntityGoal<Illusioner>(this, Illusioner.class, 12.0f, 0.5, 0.5));
        this.goalSelector.addGoal(1, new AvoidEntityGoal<Zoglin>(this, Zoglin.class, 10.0f, 0.5, 0.5));
        this.goalSelector.addGoal(1, new PanicGoal(this, 0.75));
        this.goalSelector.addGoal(2, new MoveToPointGoal(this));
        this.goalSelector.addGoal(4, new MoveTowardsRestrictionGoal(this, 0.35));
        this.goalSelector.addGoal(8, new WaterAvoidingRandomStrollGoal(this, 0.35));
        this.goalSelector.addGoal(9, new InteractGoal(this, Player.class, 3.0f, 1.0f));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 8.0f));
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand interactionHand) {

        this.getEntityData().set(MOVE_TARGET, player.blockPosition().offset(50, 0, 50));
        this.getEntityData().set(HAS_MOVE_TARGET, true);

        LogUtils.getLogger().info("Civie move target set: {}", player.blockPosition());

        return InteractionResult.SUCCESS;
    }

    public @Nullable BlockPos getMoveTarget() {
        if (!this.getEntityData().get(HAS_MOVE_TARGET))
            return null;

        return this.getEntityData().get(MOVE_TARGET);
    }

    public void clearMoveTarget() {
        this.getEntityData().set(HAS_MOVE_TARGET, false);
    }
}
