package com.uncreated.civilized.neoforge.registration.entity;

import com.uncreated.civilized.entity.renderer.CivilizedVillagerRenderer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;


public class EntitySetupEventsClient {

    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(EntityRegistry.CIVILIZED_VILLAGER.get(), CivilizedVillagerRenderer::new);
    }
}
