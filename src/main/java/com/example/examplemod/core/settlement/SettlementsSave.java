package com.example.examplemod.core.settlement;

import com.google.common.collect.ImmutableList;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.*;

public class SettlementsSave extends SavedData {

    private List<Settlement> settlements;

    public SettlementsSave() {
        settlements = new ArrayList<>();
    }

    // Create new instance of saved data
    public static SettlementsSave create() {
        return new SettlementsSave();
    }

    // Load existing instance of saved data
    public static SettlementsSave load(CompoundTag tag, HolderLookup.Provider lookupProvider) {
        return createFromCompoundTag(tag);
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {

        ListTag tags = new ListTag();
        for (Settlement settlement : settlements) {
            CompoundTag item = new CompoundTag();
            item.putInt("settlementId", settlement.getSettlementId());
            tags.add(item);
        }

        tag.put("settlements", tags);
        return tag;
    }

    public ImmutableList<Settlement> allSettlements() {
        return ImmutableList.copyOf(settlements);
    }

    public Settlement addNewSettlement() {
        Settlement settlement = new Settlement(settlements.size());
        settlements.add(settlement);
        return settlement;
    }

    public static SettlementsSave createFromCompoundTag(CompoundTag compoundTag) {
        SettlementsSave store = new SettlementsSave();

        ListTag list = compoundTag.getList("settlements", Tag.TAG_COMPOUND);
        for (Tag tag : list) {
            if (!(tag instanceof CompoundTag ct)) {
                continue;
            }
            int settlementId = ct.getInt("settlementId");
            Settlement settlement = new Settlement(settlementId);
            store.settlements.add(settlement);
        }

        return store;
    }
}
