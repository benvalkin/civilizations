package com.example.examplemod.entities.renderer;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.entities.CivilizedVillager;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.resources.ResourceLocation;

public class CivilizedVillagerRenderer extends HumanoidMobRenderer<CivilizedVillager, HumanoidRenderState, HumanoidModel<HumanoidRenderState>> {

    public static final ResourceLocation TEXTURE_LOCATION = ResourceLocation.fromNamespaceAndPath(ExampleMod.MODID, "textures/entity/civilized_villager.png");

    public CivilizedVillagerRenderer(EntityRendererProvider.Context context) {
        super(context, new HumanoidModel<>(context.bakeLayer(ModelLayers.PLAYER)), 1);
    }

    @Override
    public HumanoidRenderState createRenderState() {
        return new HumanoidRenderState();
    }


    @Override
    public ResourceLocation getTextureLocation(HumanoidRenderState renderState) {
        return TEXTURE_LOCATION;
    }
}
