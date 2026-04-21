package com.uncreated.civilized.core.building.state;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.uncreated.civilized.core.building.Building;
import com.uncreated.civilized.core.building.production.RecipeProductionMachine;
import com.uncreated.civilized.core.building.production.bills.ProductionBill;
import com.uncreated.civilized.core.building.production.bills.ProductionType;
import com.uncreated.civilized.core.building.production.bills.strategy.ProductionStrategyType;
import com.uncreated.civilized.core.building.production.orders.ProductionOrder;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;

public abstract class ArtisanHouseState extends BuildingState {

   private final Map<ProductionType, List<ProductionBill>> productionLines;

   protected ArtisanHouseState(Building building) {
      super(building);

      productionLines = new HashMap<>();
      for (ProductionType productionType : getSupportedProductionTypes())
         productionLines.put(productionType, new ArrayList<>());

      assert !getSupportedProductionTypes().isEmpty() : "House must support at least one ProductionType.";
   }

   public <Order extends ProductionOrder> void createProductionOrders(
         RecipeProductionMachine<Order> machine,
         ServerLevel serverLevel) {

      RecipeManager recipeManager = serverLevel.getServer().getRecipeManager();

      List<ProductionBill> productionBills =
            this.productionLines.getOrDefault(machine.getProductionType(), new ArrayList<>());

      for (int i = 0; i < productionBills.size(); i++) {

         ProductionBill bill = productionBills.get(i);

         Optional<RecipeHolder<?>> recipe = bill.resolveRecipe(recipeManager);

         if (recipe.isEmpty())
            continue;

         String key = "productionBill_" + i;
         Order order = machine.createOrderFromBill(key, bill, serverLevel);
         machine.registerOrder(order);
      }
   }

   public void applyNbt(CompoundTag compoundTag, HolderLookup.Provider registryAccess) {
      for (ProductionType productionType : getSupportedProductionTypes()) {
         readProductionLine(compoundTag, registryAccess, productionType);
      }
   }

   private void readProductionLine(
         CompoundTag compoundTag,
         HolderLookup.Provider registryAccess,
         ProductionType productionType) {

      if (!getSupportedProductionTypes().contains(productionType))
         return;

      String key = "production_bills_" + productionType.name().toLowerCase();
      if (!compoundTag.contains(key))
         return;

      CompoundTag billsTag = compoundTag.getCompound(key);
      List<ProductionBill> productionBills = new ArrayList<>();
      ListTag list = billsTag.getList("production_bills", ListTag.TAG_COMPOUND);
      for (Tag tag : list) {
         if (!(tag instanceof CompoundTag bt))
            continue;

         ProductionBill bill =
               new ProductionBill(
                     bt.getString("recipe_name"),
                     ProductionType.valueOf(bt.getString("production_type")),
                     ProductionStrategyType.valueOf(bt.getString("production_strategy_type")),
                     bt.getInt("bill_amount"),
                     bt.getBoolean("enabled"),
                     readItemList(bt.getList("input_items", Tag.TAG_COMPOUND), registryAccess),
                     ItemStack.parse(registryAccess, bt.getCompound("display_item")).orElse(ItemStack.EMPTY));
         productionBills.add(bill);

         productionLines.put(productionType, productionBills);
      }
   }

   public CompoundTag toNbt(HolderLookup.Provider registryAccess) {
      CompoundTag tag = new CompoundTag();

      for (ProductionType productionType : productionLines.keySet()) {
         writeProductionBills(registryAccess, tag, productionType);
      }

      return tag;
   }

   private void writeProductionBills(
         HolderLookup.Provider registryAccess,
         CompoundTag rootTag,
         ProductionType productionType) {
      CompoundTag tag = new CompoundTag();
      ListTag list = new ListTag();

      List<ProductionBill> productionBills = this.productionLines.get(productionType);
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
      rootTag.put("production_bills_" + productionType.name().toLowerCase(), tag);
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

   public Optional<List<ProductionBill>> tryGetProductionBills(ProductionType productionType) {
      return Optional.ofNullable(productionLines.get(productionType));
   }

   public List<ProductionBill> getProductionBills(ProductionType productionType) {
      return tryGetProductionBills(productionType).orElseThrow(
            () -> new UnsupportedOperationException(
                  "Artisan building does not support production type: " + productionType));
   }

   public void addBill(ProductionBill productionBill) {
      productionLines.computeIfPresent(productionBill.getProductionType(), (type, bills) -> {
         bills.add(productionBill);
         return bills;
      });
   }

   public void removeBill(ProductionType productionType, int index) {
      productionLines.computeIfPresent(productionType, (type, bills) -> {
         bills.remove(index);
         return bills;
      });
   }

   public void replaceBill(int index, ProductionBill productionBill) {
      productionLines.computeIfPresent(productionBill.getProductionType(), (type, bills) -> {
         bills.set(index, productionBill);
         return bills;
      });

   }

   protected abstract List<ProductionType> getSupportedProductionTypes();

   protected abstract List<ProductionBill> getDefaultProductionBills(ProductionType productionType);

   public boolean tryLoadDefaultProductionBills() {

      boolean result = false;
      for (Map.Entry<ProductionType, List<ProductionBill>> line : productionLines.entrySet()) {
         if (line.getValue().isEmpty()) {
            productionLines.put(line.getKey(), getDefaultProductionBills(line.getKey()));
            result = true;
         }
      }

      return result;
   }
}
