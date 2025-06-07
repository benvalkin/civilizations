package com.example.examplemod.core.settlement;

import com.mojang.logging.LogUtils;
import net.minecraft.world.level.saveddata.SavedData;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;


public class TestEvent {

    @SubscribeEvent
    public static void playerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {

        SettlementsSave store = event.getEntity().getServer().overworld().getDataStorage().computeIfAbsent(new SavedData.Factory<>(SettlementsSave::create, SettlementsSave::load), "settlements");

        LogUtils.getLogger().info("LOADED SETTLEMENTS: {}", store.allSettlements().size());

        store.addNewSettlement();
        store.setDirty();

    }
}
