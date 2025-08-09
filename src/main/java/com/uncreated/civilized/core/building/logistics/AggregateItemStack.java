package com.uncreated.civilized.core.building.logistics;

import java.util.List;

import org.apache.commons.compress.utils.Lists;

import lombok.Getter;
import net.minecraft.world.item.ItemStack;

@Getter
public class AggregateItemStack {
    private final List<ItemStack> itemStacks;
    private int count;

    public AggregateItemStack(List<ItemStack> itemStacks) {
        this.itemStacks = itemStacks;
        this.count = itemStacks.stream().mapToInt(ItemStack::getCount).sum();
    }

    public AggregateItemStack() {
        this.itemStacks = Lists.newArrayList();
        this.count = 0;
    }

    public void add(ItemStack itemStack) {
        this.itemStacks.add(itemStack);
        this.count += itemStack.getCount();
    }
}
