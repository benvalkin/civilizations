package com.example.examplemod.neoforge.registration;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.entities.CivilizedVillager;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class EntityRegistrations {
    public static final DeferredRegister.Entities ENTITIES =
            DeferredRegister.createEntities(ExampleMod.MODID);
    public static final Supplier<EntityType<CivilizedVillager>> CIVILIZED_VILLAGER = ENTITIES.register(
            "civilized_villager",
            // The entity type, created using a builder.
            () -> EntityType.Builder.of(
                            // An EntityType.EntityFactory<T>, where T is the entity class used - MyEntity in this case.
                            // You can think of it as a BiFunction<EntityType<T>, Level, T>.
                            // This is commonly a reference to the entity constructor.
                            CivilizedVillager::new,
                            // The MobCategory our entity uses. This is mainly relevant for spawning.
                            // See below for more information.
                            MobCategory.MISC
                    )
                    // Disables a rule in the spawn handler that limits the distance at which entities can spawn.
                    // This means that no matter the distance to the player, this entity can spawn.
                    // Vanilla enables this for pillagers and shulkers.
                    .canSpawnFarFromPlayer()
                    // The range in which the entity is kept loaded by the client, in chunks.
                    // Vanilla values for this vary, but it's often something around 8 or 10. Defaults to 5.
                    // Be aware that if this is greater than the client's chunk view distance,
                    // then that chunk view distance is effectively used here instead.
                    .clientTrackingRange(10)
                    // Build the entity type using a resource key. The second parameter should be the same as the entity id.
                    .build(ResourceKey.create(
                            Registries.ENTITY_TYPE,
                            ResourceLocation.fromNamespaceAndPath("examplemod", "civilized_villager")
                    ))
    );
}
