package com.uncreated.civilized.core.building.logistics.imports;

import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.stream.Stream;

import org.jetbrains.annotations.Nullable;

import com.uncreated.civilized.core.building.Building;
import com.uncreated.civilized.core.building.logistics.AggregateItemStack;
import com.uncreated.civilized.core.building.logistics.PendingShipment;
import com.uncreated.civilized.entity.CivilizedVillager;
import com.uncreated.civilized.neoforge.registration.ai.AIRegistry;
import com.uncreated.civilized.util.ContainerHelper;

import lombok.Getter;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.ChestBlockEntity;

@Getter
public abstract class ImportOrder {

   private final String key;
   private final ItemStack itemType;
   @Nullable
   private Instant startTime;
   @Nullable
   private Integer expiryTimeMinutes;

   public ImportOrder(String key, ItemStack itemType) {
      this.itemType = itemType;
      this.key = key;
   }

   public ImportOrder withExpiryTime(int expiryTimeMinutes) {
      this.expiryTimeMinutes = expiryTimeMinutes;
      this.startTime = Instant.now();
      return this;
   }

   public boolean isExpired() {
      if (expiryTimeMinutes == null || startTime == null)
         return false;

      return Duration.between(startTime, Instant.now()).getSeconds() > expiryTimeMinutes * 60;
   }

   protected abstract boolean shouldImport(
         AggregateItemStack exportSourceStock,
         AggregateItemStack importDestinationStock);

   protected abstract int getItemCountForNextShipment(
         AggregateItemStack exportSourceStock,
         AggregateItemStack importDestinationStock);

   public PendingShipment getNextShipment(Building exportSource, Building importDestination, Level level) {
      Stream<Container> exportSourceChests =
            exportSource.getBounds()
                  .getBlockEntitiesInsideBuilding(level)
                  .stream()
                  .filter(e -> e instanceof ChestBlockEntity)
                  .map(e -> ((Container) e));

      Stream<Container> importSourceChests =
            importDestination.getBounds()
                  .getBlockEntitiesInsideBuilding(level)
                  .stream()
                  .filter(e -> e instanceof ChestBlockEntity)
                  .map(e -> (ChestBlockEntity) e);

      AggregateItemStack exportSourceStock = calculateStock(exportSourceChests);
      AggregateItemStack importDestinationStock = calculateStock(importSourceChests);

      int amountToShip = getItemCountForNextShipment(exportSourceStock, importDestinationStock);

      if (!shouldImport(exportSourceStock, importDestinationStock) || amountToShip <= 0)
         return new PendingShipment(itemType, amountToShip, false);

      return new PendingShipment(itemType, amountToShip, true);
   }

   public void takeShipment(
         CivilizedVillager villager,
         Collection<Container> sourceContainers,
         PendingShipment pendingShipment) {

      int quota = pendingShipment.getAmount();

      for (Container source : sourceContainers) {
         int transferred =
               ContainerHelper.transferNicely(
                     source,
                     villager.getLogisticsInventory(),
                     it -> !it.isEmpty() && it.is(itemType.getItem()),
                     quota);

         quota -= transferred;

         if (quota <= 0)
            break;
      }

      if (quota < pendingShipment.getAmount())
         villager.getBrain().setMemory(AIRegistry.MM_HOLDING_LOGISTICS_RESOURCES.get(), true);
   }

   protected AggregateItemStack calculateStock(Stream<Container> sourceContainers) {
      AggregateItemStack stock = new AggregateItemStack();
      sourceContainers.forEach(c -> {
         for (int i = 0; i < c.getContainerSize(); i++) {
            ItemStack itemStack = c.getItem(i);
            if (itemStack.isEmpty())
               continue;

            if (!itemStack.is(itemType.getItem()))
               continue;

            stock.add(itemStack);
         }
      });

      return stock;
   }
}
