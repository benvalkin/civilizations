package com.uncreated.civilized.core.building.state;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import com.uncreated.civilized.core.building.Building;
import com.uncreated.civilized.core.building.crafting.bills.ProductionBill;
import com.uncreated.civilized.core.building.crafting.bills.ProductionType;
import com.uncreated.civilized.core.building.crafting.bills.strategy.ProductionStrategyType;
import com.uncreated.civilized.core.building.crafting.orders.ProductionOrder;

import lombok.Getter;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;

public class ArtisanHouseState extends BuildingState {

   @Getter
   private List<ProductionBill> productionBills;

   private boolean shouldLoadDefaults;

   protected ArtisanHouseState(Building building) {
      super(building);

      productionBills = new ArrayList<>();
      shouldLoadDefaults = true;
   }

   public List<ProductionOrder> createProductionOrders(ServerLevel serverLevel) {

      RecipeManager recipeManager = serverLevel.getServer().getRecipeManager();

      List<ProductionOrder> productionOrders = new LinkedList<>();
      for (int i = 0; i < productionBills.size(); i++) {

         ProductionBill bill = productionBills.get(i);

         Optional<RecipeHolder<?>> recipe = bill.resolveRecipe(recipeManager);

         if (recipe.isEmpty())
            continue;

         String key = "order_" + i;
         productionOrders.add(new ProductionOrder(key, bill, serverLevel));
      }

      return productionOrders;
   }

   public void applyNbt(CompoundTag compoundTag, HolderLookup.Provider registryAccess) {

      productionBills = new ArrayList<>();

      ListTag list = compoundTag.getList("production_bills", ListTag.TAG_COMPOUND);
      for (Tag tag : list) {
         if (!(tag instanceof CompoundTag ct))
            continue;

         ProductionBill bill =
               new ProductionBill(
                     ct.getString("recipe_name"),
                     ProductionType.valueOf(ct.getString("production_type")),
                     ProductionStrategyType.valueOf(ct.getString("production_strategy_type")),
                     ct.getInt("bill_amount"),
                     ct.getBoolean("enabled"),
                     readItemList(ct.getList("input_items", Tag.TAG_COMPOUND), registryAccess),
                     ItemStack.parse(registryAccess, ct.getCompound("display_item")).orElse(ItemStack.EMPTY));
         productionBills.add(bill);
      }

      shouldLoadDefaults = compoundTag.getBoolean("should_load_defaults");
   }

   public CompoundTag toNbt(HolderLookup.Provider registryAccess) {
      CompoundTag tag = new CompoundTag();

      ListTag list = new ListTag();

      for (ProductionBill bill : productionBills) {
         CompoundTag billTag = new CompoundTag();
         billTag.putString("recipe_name", bill.getMinecraftRecipeName());
         billTag.putString("production_type", bill.getProductionType().name());
         billTag.putString("production_strategy_type", bill.getProductionStrategy().getType().name());
         billTag.putInt("bill_amount", bill.getBillAmount());
         billTag.putBoolean("enabled", bill.isEnabled());
         billTag.put("input_items", writeItemList(bill.getInputItems(), registryAccess));
         billTag.put("display_item", bill.getDisplayItem().save(registryAccess, new CompoundTag()));
         list.add(billTag);
      }

      tag.put("production_bills", list);
      tag.putBoolean("should_load_defaults", shouldLoadDefaults);

      return tag;
   }

   private List<ItemStack> readItemList(ListTag listTag, HolderLookup.Provider registryAccess) {
      List<ItemStack> result = new ArrayList<>();
      for (Tag tag : listTag) {
         if (!(tag instanceof CompoundTag itemTag))
            continue;

         ItemStack itemStack = ItemStack.parseOptional(registryAccess, itemTag);
         result.add(itemStack);
      }
      return result;
   }

   private ListTag writeItemList(List<ItemStack> items, HolderLookup.Provider registryAccess) {
      ListTag list = new ListTag();
      for (ItemStack item : items) {
         list.add(item.saveOptional(registryAccess));
      }

      return list;
   }

   public void addBill(ProductionBill productionBill) {
      productionBills.add(productionBill);
   }

   public void removeBill(int index) {
      productionBills.remove(index);
   }

   public void replaceBill(int index, ProductionBill productionBill) {
      productionBills.set(index, productionBill);
   }

   protected List<ProductionBill> getDefaultProductionBills() {
      return List.of();
   }

   public boolean tryLoadDefaultProductionBills() {
      if (!shouldLoadDefaults)
         return false;

      productionBills = getDefaultProductionBills();
      shouldLoadDefaults = false;
      return true;
   }
}
