package com.uncreated.civilized.entity.renderer;

import com.uncreated.civilized.core.villagerinfo.VillagerOccupation;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;

public class CivilizedVillagerRenderState extends HumanoidRenderState {

    public VillagerOccupation occupation;
    @Nullable
    public Component villagerName;
    public Component occupationName;
    public ResourceLocation skin;
    public ResourceLocation clothing;

    public CivilizedVillagerRenderState() {

    }
}
