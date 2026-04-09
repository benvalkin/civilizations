package com.uncreated.civilized.core.building.production;

import java.util.LinkedList;
import java.util.List;

import com.uncreated.civilized.core.building.production.bills.ProductionBill;
import com.uncreated.civilized.core.building.production.bills.ProductionType;
import com.uncreated.civilized.core.building.production.orders.ProductionOrder;
import net.minecraft.server.level.ServerLevel;

public abstract class RecipeProductionMachine<Order extends ProductionOrder> {

   private final List<Order> orders;

   public RecipeProductionMachine() {
      orders = new LinkedList<>();
   }

   public void registerOrder(Order productionOrder) {
      orders.add(productionOrder);
   }

   public List<Order> getOrders() {
      return orders.reversed();
   }

   public abstract Order createOrder(String key, ProductionBill bill, ServerLevel serverLevel);

   public abstract ProductionType getProductionType();
}
