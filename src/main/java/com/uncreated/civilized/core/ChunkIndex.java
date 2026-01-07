package com.uncreated.civilized.core;

import java.util.Set;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;

public class ChunkIndex<V> extends SetIndex<ChunkPos, V> {

    public void add(BlockPos blockPos, V obj) {
        super.add(new ChunkPos(blockPos), obj);
    }

    public void remove(BlockPos blockPos, V obj) {
        super.remove(new ChunkPos(blockPos), obj);
    }

    @Override
    public Set<V> getValues(ChunkPos chunkPos) {
        return super.getValues(chunkPos);
    }
}
