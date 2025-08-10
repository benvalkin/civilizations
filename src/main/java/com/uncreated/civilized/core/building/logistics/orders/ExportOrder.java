package com.uncreated.civilized.core.building.logistics.orders;

import net.minecraft.world.item.ItemStack;

import java.util.function.Predicate;

public abstract class ExportOrder extends LogisticsOrder  {
    public ExportOrder(String key, Predicate<ItemStack> itemSearch, Origin origin) {
        super(key, itemSearch, origin);
    }
}
