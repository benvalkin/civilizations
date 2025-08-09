package com.uncreated.civilized.core.building.logistics.imports;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import com.uncreated.civilized.core.building.Building;
import lombok.Getter;

public class ImportOrders {
   @Getter
   private final Building building;
   private final HashMap<String, ImportOrder> orders;

   public ImportOrders(Building building) {
      this.building = building;
      this.orders = new HashMap<>();
   }

   public void add(ImportOrder order) {
      orders.put(order.getKey(), order);
   }

   public Collection<ImportOrder> all() {
      Set<String> expiredKeys = new HashSet<>();
      for (ImportOrder order : orders.values()) {
         if (order.isExpired())
            expiredKeys.add(order.getKey());
      }

      expiredKeys.forEach(orders::remove);
      return orders.values();
   }
}
