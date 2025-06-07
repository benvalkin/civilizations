package com.example.examplemod.entities.events;

import com.example.examplemod.neoforge.registration.EntityRegistrations;
import com.example.examplemod.entities.renderer.CivilizedVillagerRenderer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;


public class EntitySetupEventsClient {

    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(EntityRegistrations.CIVILIZED_VILLAGER.get(), CivilizedVillagerRenderer::new);
    }
}
