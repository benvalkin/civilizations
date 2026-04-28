package com.uncreated.civilized.core.building.state;

import com.uncreated.civilized.core.building.Building;
import com.uncreated.civilized.core.settlement.Settlement;
import com.uncreated.civilized.ui.menu.building.item.management.ItemManagementMenu;

import net.minecraft.world.entity.player.Inventory;

public interface IItemManagementMenuSupplier {
   ItemManagementMenu createItemManagementMenu(
         Integer containerId,
         Inventory playerInventory,
         Building building,
         Settlement settlement);
}
