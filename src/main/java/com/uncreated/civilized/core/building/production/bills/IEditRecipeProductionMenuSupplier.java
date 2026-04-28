package com.uncreated.civilized.core.building.production.bills;

import com.uncreated.civilized.core.building.Building;
import com.uncreated.civilized.core.settlement.Settlement;
import com.uncreated.civilized.ui.menu.building.residence.artisan.EditRecipeMenu;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;

public interface IEditRecipeProductionMenuSupplier {
   EditRecipeMenu<?, ?> createMenu(
            int containerId,
            Inventory playerInventory,
            Settlement settlement,
            Building building,
            int productionBillIndex,
            boolean isNewBill);
}
