package com.uncreated.civilized.neoforge.registration.gui;

import static com.uncreated.civilized.CivilizedMod.CIVILIZED_MOD_ID;

import java.util.function.Supplier;

import com.uncreated.civilized.ui.MyMenu;
import com.uncreated.civilized.ui.TestContainerMenu;
import com.uncreated.civilized.ui.menu.building.BuildingMenu;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredRegister;

public class GuiRegistry {
   public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(BuiltInRegistries.MENU, CIVILIZED_MOD_ID);

   public static final Supplier<MenuType<MyMenu>> MY_MENU =
         MENUS.register("my_menu", () -> IMenuTypeExtension.create(MyMenu::new));

   public static final Supplier<MenuType<TestContainerMenu>> TEST_CONTAINER_MENU =
         MENUS.register(
               "test_container_screen",
               () -> new MenuType<>(TestContainerMenu::new, FeatureFlags.DEFAULT_FLAGS));

   public static final Supplier<MenuType<BuildingMenu>> BUILDING_MENU =
         MENUS.register("building_menu", () -> IMenuTypeExtension.create(BuildingMenu::new));
}
