package com.uncreated.civilized.core.building.logistics;

import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

import com.uncreated.civilized.core.building.Building;
import com.uncreated.civilized.core.building.logistics.orders.ExportOrder;
import com.uncreated.civilized.core.building.logistics.orders.ImportOrder;
import com.uncreated.civilized.core.building.logistics.orders.LogisticsOrder;
import com.uncreated.civilized.core.building.logistics.orders.LogisticsOrders;

public class LogisticsManager {
   private final HashMap<UUID, LogisticsOrders> importOrders;
   private final HashMap<UUID, LogisticsOrders> exportOrders;

   public LogisticsManager() {
      importOrders = new HashMap<>();
      exportOrders = new HashMap<>();
   }

   public void registerOrder(Building building, LogisticsOrder order) {
      HashMap<UUID, LogisticsOrders> store = order instanceof ImportOrder ? importOrders : exportOrders;
      store.computeIfAbsent(building.getBuildingId(), (buildingId) -> new LogisticsOrders(building));
      store.computeIfPresent(building.getBuildingId(), (buildingId, orders) -> {
         orders.add(order);
         return orders;
      });
   }

   public void registerOrder(Building building, LogisticsOrder order, int expiryTimeMinutes) {
      HashMap<UUID, LogisticsOrders> store = order instanceof ImportOrder ? importOrders : exportOrders;
      store.computeIfAbsent(building.getBuildingId(), (buildingId) -> new LogisticsOrders(building));
      store.computeIfPresent(building.getBuildingId(), (buildingId, orders) -> {
         orders.add(order.withExpiryTime(expiryTimeMinutes));
         return orders;
      });
   }

   public LogisticsOrders getImportOrders(Building building) {
      return Optional.ofNullable(importOrders.get(building.getBuildingId())).orElse(new LogisticsOrders(building));
   }

   public LogisticsOrders getExportOrders(Building building) {
      return Optional.ofNullable(exportOrders.get(building.getBuildingId())).orElse(new LogisticsOrders(building));
   }
}