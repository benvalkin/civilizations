package com.example.examplemod.core.settlement;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.saveddata.SavedData;

public class Settlement {

    private int settlementId;

    public Settlement(int settlementId) {
        this.settlementId = settlementId;
    }

    public int getSettlementId() {
        return settlementId;
    }
}
