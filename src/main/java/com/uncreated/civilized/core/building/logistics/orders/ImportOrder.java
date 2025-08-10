package com.uncreated.civilized.core.building.logistics.orders;

import net.minecraft.world.item.ItemStack;

import java.util.function.Predicate;

public abstract class ImportOrder extends LogisticsOrder  {
    public ImportOrder(String key, Predicate<ItemStack> itemSearch, Origin origin) {
        super(key, itemSearch, origin);
    }
}
