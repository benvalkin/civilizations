package com.uncreated.civilized.ui.menu.building.residence.artisan.singleitem;

import java.util.List;
import java.util.Optional;

import com.mojang.datafixers.util.Pair;
import com.uncreated.civilized.core.building.Building;
import com.uncreated.civilized.core.building.production.bills.ProductionType;
import com.uncreated.civilized.core.settlement.Settlement;
import com.uncreated.civilized.neoforge.registration.gui.GuiRegistry;
import com.uncreated.civilized.ui.menu.building.residence.artisan.EditRecipeMenu;
import com.uncreated.civilized.ui.menu.item.management.EyedropperSlot;

import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.BlastingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;

public class EditBlastingRecipeMenu extends EditRecipeMenu<BlastingRecipe, SingleRecipeInput> {

   public EditBlastingRecipeMenu(int containerId, Inventory playerInventory, FriendlyByteBuf extraDataFromServer) {
      super(GuiRegistry.EDIT_BLASTING_RECIPE_MENU.get(), containerId, playerInventory, extraDataFromServer);
   }

   public EditBlastingRecipeMenu(
         int containerId,
         Inventory playerInventory,
         Container craftingMenuContainer,
         Settlement settlement,
         Building building,
         int productionBillIndex,
         boolean isNewBill) {
      super(
            GuiRegistry.EDIT_BLASTING_RECIPE_MENU.get(),
            containerId,
            playerInventory,
            craftingMenuContainer,
            settlement,
            building,
            productionBillIndex,
            isNewBill);
   }

   @Override
   protected List<EyedropperSlot> setupInputSlots(Container craftingMenuContainer) {
      return List.of(new EyedropperSlot(craftingMenuContainer, 0, 51 + 18, 20 + 18));
   }

   @Override
   public ProductionType getProductionType() {
      return ProductionType.BLASTING;
   }

   @Override
   protected Optional<Pair<RecipeHolder<BlastingRecipe>, SingleRecipeInput>> getRecipeFromInputContainer(
         NonNullList<ItemStack> recipeInputItems,
         ServerLevel serverLevel,
         RecipeManager recipeManager) {

      SingleRecipeInput recipeInput = new SingleRecipeInput(recipeInputItems.getFirst());

      Optional<RecipeHolder<BlastingRecipe>> recipe =
            recipeManager.getRecipeFor(RecipeType.BLASTING, recipeInput, serverLevel);

      return recipe.map(craftingRecipeRecipeHolder -> Pair.of(craftingRecipeRecipeHolder, recipeInput));
   }
}
