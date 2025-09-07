package com.uncreated.civilized.neoforge.registration.gui;

import static com.uncreated.civilized.CivilizedMod.CIVILIZED_MOD_ID;

import java.util.function.Supplier;

import com.uncreated.civilized.ui.menu.building.worksite.cropfarm.items.ChooseCropsMenu;
import com.uncreated.civilized.ui.menu.building.worksite.grove.items.ChooseSaplingsMenu;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredRegister;

public class GuiRegistry {
   public static final DeferredRegister<MenuType<?>> MENUS =
         DeferredRegister.create(BuiltInRegistries.MENU, CIVILIZED_MOD_ID);

   public static final Supplier<MenuType<ChooseCropsMenu>> CHOOSE_CROPS_MENU =
         MENUS.register("choose_crops_menu", () -> IMenuTypeExtension.create(ChooseCropsMenu::new));

   public static final Supplier<MenuType<ChooseSaplingsMenu>> CHOOSE_SAPLINGS_MENU =
         MENUS.register("choose_saplings_menu", () -> IMenuTypeExtension.create(ChooseSaplingsMenu::new));
}
