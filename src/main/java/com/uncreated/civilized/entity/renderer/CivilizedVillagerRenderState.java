package com.uncreated.civilized.entity.renderer;

import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.network.chat.Component;

import javax.annotation.Nullable;

public class CivilizedVillagerRenderState extends HumanoidRenderState {

    @Nullable
    public Component villagerName;
    public Component jobName;

    public CivilizedVillagerRenderState() {

    }
}
