package com.example.examplemod.entities.events;

import com.example.examplemod.neoforge.registration.EntityRegistrations;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;


public class EntitySetupEventsCommon {

    @SubscribeEvent
    public static void createDefaultAttributes(EntityAttributeCreationEvent event) {
        event.put(
                // Your entity type.
                EntityRegistrations.CIVILIZED_VILLAGER.get(),
                // An AttributeSupplier. This is typically created by calling LivingEntity#createLivingAttributes,
                // setting your values on it, and calling #build. You can also create the AttributeSupplier from scratch
                // if you want, see the source of LivingEntity#createLivingAttributes for an example.
                LivingEntity.createLivingAttributes()
                        // Add an attribute with a non-default value.
                        .add(Attributes.MAX_HEALTH)
                        .add(Attributes.FOLLOW_RANGE)
                        // Build the AttributeSupplier.
                        .build()
        );
    }
}
