package com.uncreated.civilized.core;

import java.util.Set;

import net.minecraft.core.BlockPos;

public class ProximityIndex<V> extends SetIndex<BlockPos, V> {

    private final int spacing;

    public ProximityIndex(int spacing) {
        this.spacing = spacing;
    }

    public void add(BlockPos blockPos, V obj) {
        super.add(computeIndexBlockPos(blockPos), obj);
    }

    public void remove(BlockPos blockPos, V obj) {
        super.remove(computeIndexBlockPos(blockPos), obj);
    }

    @Override
    public Set<V> getValues(BlockPos blockPos) {
        return super.getValues(computeIndexBlockPos(blockPos));
    }

    private BlockPos computeIndexBlockPos(BlockPos blockPos) {
        int indexX = findNearestMultiple(blockPos.getX());
        int indexY = findNearestMultiple(blockPos.getY());
        int indexZ = findNearestMultiple(blockPos.getZ());
        return new BlockPos(indexX, indexY, indexZ);
    }

    private int findNearestMultiple(int n) {
        int remainder = n % spacing;

        int lowerMultiple = n - remainder;
        int upperMultiple = n - remainder + spacing;

        if (Math.abs(n - lowerMultiple) <= Math.abs(n - upperMultiple)) {
            return lowerMultiple;
        } else {
            return upperMultiple;
        }
    }
}
