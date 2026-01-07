package com.uncreated.civilized.core.building.crafting;

import java.util.LinkedList;
import java.util.List;

import com.uncreated.civilized.core.building.crafting.orders.ProductionOrder;

public class CraftingMachine {
   private final List<ProductionOrder> orders;

   public CraftingMachine() {
      orders = new LinkedList<>();
   }

   public void registerOrder(ProductionOrder productionOrder) {
      orders.add(productionOrder);
   }

   public List<ProductionOrder> getOrders() {
      return orders.reversed();
   }
}
