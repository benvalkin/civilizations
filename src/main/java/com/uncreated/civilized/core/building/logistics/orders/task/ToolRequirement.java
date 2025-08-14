package com.uncreated.civilized.core.building.logistics.orders.task;

import com.uncreated.civilized.core.building.logistics.AggregateItemStack;
import net.minecraft.core.HolderSet;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.Container;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.level.block.Block;

public class ToolRequirement extends TaskItemRequirement {

    public ToolRequirement(String taskName, Class<? extends Item> toolItemType, Origin origin) {
        super(taskName, i -> toolItemType.isInstance(i.getItem()), origin);
    }

    public ToolRequirement(String taskName, Class<? extends Item> toolItemType, HolderSet<Block> exampleRequiredMinableBlocks, Origin origin) {
        super(taskName, i -> {

            if (!toolItemType.isInstance(i.getItem()))
                return false;

            Tool tool = i.get(DataComponents.TOOL);
            if (tool == null)
                return false;

            for (Tool.Rule rule : tool.rules()) {
                if (exampleRequiredMinableBlocks.stream().allMatch(b -> rule.blocks().contains(b)))
                    return true;
            }

            return false;

        }, origin);
    }

    @Override
    protected boolean canTake(AggregateItemStack sourceStock) {
        return sourceStock.getCount() > 0;
    }

    @Override
    protected boolean villagerHasRequiredItems(Container villagerInventory) {
        return villagerInventory.hasAnyMatching(itemSearch);
    }

    @Override
    protected int getItemCountRequiredForTask(AggregateItemStack sourceStock) {
        return 1;
    }
}
