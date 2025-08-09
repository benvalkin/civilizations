package com.uncreated.civilized.core.building.logistics;

import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

import com.uncreated.civilized.core.building.Building;
import com.uncreated.civilized.core.building.logistics.imports.ImportOrder;
import com.uncreated.civilized.core.building.logistics.imports.ImportOrders;

public class LogisticsManager {
   private final HashMap<UUID, ImportOrders> importOrders;

   public LogisticsManager() {
      importOrders = new HashMap<>();
   }

   public void registerImportOrder(Building building, ImportOrder importOrder) {
      importOrders.computeIfAbsent(building.getBuildingId(), (buildingId) -> new ImportOrders(building));
      importOrders.computeIfPresent(building.getBuildingId(), (buildingId, orders) -> {
         orders.add(importOrder);
         return orders;
      });
   }

   public void registerImportOrder(Building building, ImportOrder importOrder, int expiryTimeMinutes) {
      importOrders.computeIfAbsent(building.getBuildingId(), (buildingId) -> new ImportOrders(building));
      importOrders.computeIfPresent(building.getBuildingId(), (buildingId, orders) -> {
         orders.add(importOrder.withExpiryTime(expiryTimeMinutes));
         return orders;
      });
   }

   public ImportOrders getImportOrders(Building building) {
      return Optional.ofNullable(importOrders.get(building.getBuildingId())).orElse(new ImportOrders(building));
   }
}
