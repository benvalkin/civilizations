package com.uncreated.civilized.core.building.logistics.orders;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import com.uncreated.civilized.core.building.Building;
import lombok.Getter;

public class LogisticsOrders {
   @Getter
   private final Building building;
   private final HashMap<String, LogisticsOrder> orders;

   public LogisticsOrders(Building building) {
      this.building = building;
      this.orders = new HashMap<>();
   }

   public void add(LogisticsOrder order) {

      orders.putIfAbsent(order.getKey(), order);
      orders.computeIfPresent(order.getKey(), (buildingId, existing) -> {

         if (order.getOrigin() == LogisticsOrder.Origin.PLAYER_CREATED && existing.getOrigin() == LogisticsOrder.Origin.AUTOMATIC)
            return order;

         if (order.getOrigin() == LogisticsOrder.Origin.AUTOMATIC && existing.getOrigin() == LogisticsOrder.Origin.PLAYER_CREATED)
            return existing;

         return order;
      });
   }

   public Collection<LogisticsOrder> orders() {
      Set<String> expiredKeys = new HashSet<>();
      for (LogisticsOrder order : orders.values()) {
         if (order.isExpired())
            expiredKeys.add(order.getKey());
      }

      expiredKeys.forEach(orders::remove);
      return orders.values();
   }
}
