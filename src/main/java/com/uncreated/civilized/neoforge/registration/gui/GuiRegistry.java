package com.uncreated.civilized.neoforge.registration.gui;

import static com.uncreated.civilized.CivilizedMod.CIVILIZED_MOD_ID;

import java.util.function.Supplier;

import com.uncreated.civilized.ui.menu.building.residence.artisan.EditCraftingRecipeMenu;
import com.uncreated.civilized.ui.menu.building.worksite.animalfarm.items.ChooseAnimalFoodMenu;
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

   public static final Supplier<MenuType<ChooseAnimalFoodMenu>> CHOOSE_ANIMAL_FOOD_MENU =
         MENUS.register("choose_animal_food_menu", () -> IMenuTypeExtension.create(ChooseAnimalFoodMenu::new));

   public static final Supplier<MenuType<EditCraftingRecipeMenu>> CHOOSE_CRAFTING_RECIPE_MENU =
         MENUS.register("choose_crafting_recipe_menu", () -> IMenuTypeExtension.create(EditCraftingRecipeMenu::new));
}
