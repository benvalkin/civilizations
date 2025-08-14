package com.uncreated.civilized.core.building.logistics.orders.imports;

import com.uncreated.civilized.core.building.logistics.orders.StorehouseOrder;
import net.minecraft.world.item.ItemStack;

import java.util.function.Predicate;

public abstract class ImportOrder extends StorehouseOrder {
    public ImportOrder(String key, Predicate<ItemStack> itemSearch, Origin origin) {
        super(key, itemSearch, origin);
    }
}
