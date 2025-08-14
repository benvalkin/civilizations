package com.uncreated.civilized.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.uncreated.civilized.core.StoreOperation;
import com.uncreated.civilized.core.building.Building;
import com.uncreated.civilized.core.building.ServerBuildingsStore;
import com.uncreated.civilized.core.building.util.BuildingUtil;
import com.uncreated.civilized.core.villagerinfo.ServerVillagerStore;
import com.uncreated.civilized.core.villagerinfo.VillagerInfo;
import com.uncreated.civilized.core.villagerinfo.VillagerNpcRole;
import com.uncreated.civilized.entity.CivilizedVillager;
import com.uncreated.civilized.neoforge.registration.entity.EntityRegistry;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.CompoundTagArgument;
import net.minecraft.commands.arguments.ResourceArgument;
import net.minecraft.commands.arguments.TimeArgument;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.commands.synchronization.SuggestionProviders;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Optional;

public class SummonResident {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)
                Commands.literal("summonresident")
                        .executes((stack) -> spawnResidentVillager(stack.getSource()))
                .requires((stack) -> stack.hasPermission(2))))));
    }

    private static final SimpleCommandExceptionType ERROR_NOT_PLAYER = new SimpleCommandExceptionType(Component.literal("This command cannot be called from console."));
    private static final SimpleCommandExceptionType ERROR_NOT_INSIDE_BUILDING = new SimpleCommandExceptionType(Component.literal("You must be inside a building to use this command."));
    private static final SimpleCommandExceptionType ERROR_ENTITY_SPAWN_ERROR = new SimpleCommandExceptionType(Component.literal("Something went wrong while spawning the villager."));

    private static int spawnResidentVillager(CommandSourceStack source) throws CommandSyntaxException {

        ServerLevel level = source.getLevel();
        if (!source.isPlayer())
            throw ERROR_NOT_PLAYER.create();

        Optional<Building> building = ServerBuildingsStore.INSTANCE.findEnclosingBuilding(source.getPlayer().getOnPos());
        if (building.isEmpty())
            throw ERROR_NOT_INSIDE_BUILDING.create();

        BlockPos insidePos = building.get().getBounds().findRandomInsideFloorBlock(level);

        CivilizedVillager villager =
                EntityRegistry.CIVILIZED_VILLAGER.get().spawn(level, insidePos, EntitySpawnReason.EVENT);

        if (villager == null) {
            throw ERROR_ENTITY_SPAWN_ERROR.create();
        }

        villager.getInfo().getNpcRoles().add(VillagerNpcRole.WORKER);
        villager.getInfo().setSettlementId(building.get().getSettlementId());
        villager.getInfo().setHomeBuildingId(building.get().getBuildingId());
        ServerVillagerStore.INSTANCE.setDirty();
        ServerVillagerStore.INSTANCE.replicateChange(villager.getInfo(), StoreOperation.UPDATE);
        return 1;
    }
}
