package com.uncreated.civilized.core.building.logistics.orders.exports;

import com.uncreated.civilized.core.building.logistics.orders.StorehouseOrder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.function.Predicate;

public abstract class ExportOrder extends StorehouseOrder {
    public ExportOrder(Level level, String key, Predicate<ItemStack> itemSearch, Origin origin) {
        super(level, key, itemSearch, origin);
    }
}
