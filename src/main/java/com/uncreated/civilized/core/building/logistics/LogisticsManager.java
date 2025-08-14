package com.uncreated.civilized.core.building.logistics;

import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

import com.uncreated.civilized.core.building.Building;
import com.uncreated.civilized.core.building.logistics.orders.LogisticsOrder;
import com.uncreated.civilized.core.building.logistics.orders.exports.ExportOrder;
import com.uncreated.civilized.core.building.logistics.orders.imports.ImportOrder;
import com.uncreated.civilized.core.building.logistics.orders.StorehouseOrder;
import com.uncreated.civilized.core.building.logistics.orders.LogisticsOrders;
import com.uncreated.civilized.core.building.logistics.orders.task.TaskItemRequirement;

public class LogisticsManager {
   private final HashMap<UUID, LogisticsOrders<ImportOrder>> importOrders;
   private final HashMap<UUID, LogisticsOrders<ExportOrder>> exportOrders;
   private final HashMap<UUID, LogisticsOrders<TaskItemRequirement>> taskItemRequirements;

   public LogisticsManager() {
      importOrders = new HashMap<>();
      exportOrders = new HashMap<>();
      taskItemRequirements = new HashMap<>();
   }

   public void registerOrder(Building building, ImportOrder order) {
      importOrders.computeIfAbsent(building.getBuildingId(), (buildingId) -> new LogisticsOrders<>(building));
      importOrders.computeIfPresent(building.getBuildingId(), (buildingId, orders) -> {
         orders.add(order);
         return orders;
      });
   }

   public void registerOrder(Building building, ExportOrder order) {
      exportOrders.computeIfAbsent(building.getBuildingId(), (buildingId) -> new LogisticsOrders<>(building));
      exportOrders.computeIfPresent(building.getBuildingId(), (buildingId, orders) -> {
         orders.add(order);
         return orders;
      });
   }

   public void registerOrder(Building building, TaskItemRequirement order) {
      taskItemRequirements.computeIfAbsent(building.getBuildingId(), (buildingId) -> new LogisticsOrders<>(building));
      taskItemRequirements.computeIfPresent(building.getBuildingId(), (buildingId, orders) -> {
         orders.add(order);
         return orders;
      });
   }

   public LogisticsOrders<ImportOrder> getImportOrders(Building building) {
      return Optional.ofNullable(importOrders.get(building.getBuildingId())).orElse(new LogisticsOrders<>(building));
   }

   public LogisticsOrders<ExportOrder> getExportOrders(Building building) {
      return Optional.ofNullable(exportOrders.get(building.getBuildingId())).orElse(new LogisticsOrders<>(building));
   }

   public LogisticsOrders<TaskItemRequirement> getTaskItemRequirements(Building building) {
      return Optional.ofNullable(taskItemRequirements.get(building.getBuildingId())).orElse(new LogisticsOrders<>(building));
   }
}