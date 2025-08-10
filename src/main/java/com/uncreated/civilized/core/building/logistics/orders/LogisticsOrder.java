package com.uncreated.civilized.core.building.logistics.orders;

import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.function.Predicate;

import org.jetbrains.annotations.Nullable;

import com.uncreated.civilized.core.building.Building;
import com.uncreated.civilized.core.building.logistics.AggregateItemStack;
import com.uncreated.civilized.core.building.logistics.PendingShipment;
import com.uncreated.civilized.entity.CivilizedVillager;
import com.uncreated.civilized.util.ContainerHelper;

import lombok.Getter;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.ChestBlockEntity;

@Getter
public abstract class LogisticsOrder {

    private final String key;
    protected final Predicate<ItemStack> itemSearch;
   protected final Origin origin;
   @Nullable
   private Instant startTime;
   @Nullable
   private Integer expiryTimeMinutes;

   public LogisticsOrder(String key, Predicate<ItemStack> itemSearch, Origin origin) {
       this.key = key;
       this.itemSearch = itemSearch;
      this.origin = origin;
   }

   public LogisticsOrder withExpiryTime(int expiryTimeMinutes) {
      this.expiryTimeMinutes = expiryTimeMinutes;
      this.startTime = Instant.now();
      return this;
   }

   public boolean isExpired() {
      if (expiryTimeMinutes == null || startTime == null)
         return false;

      return Duration.between(startTime, Instant.now()).getSeconds() > expiryTimeMinutes * 60;
   }

   protected abstract boolean shouldShip(
         AggregateItemStack sourceStock,
         AggregateItemStack destinationStock);

   protected abstract int getItemCountForNextShipment(
         AggregateItemStack sourceStock,
         AggregateItemStack destinationStock);

   public PendingShipment getNextShipment(Building source, Building destination, Level level) {
      Collection<Container> sourceChests =
            source.getBounds()
                  .getBlockEntitiesInsideBuilding(level)
                  .stream()
                  .filter(e -> e instanceof ChestBlockEntity)
                  .map(e -> ((Container) e)).toList();

      Collection<Container> destinationChests =
            destination.getBounds()
                  .getBlockEntitiesInsideBuilding(level)
                  .stream()
                  .filter(e -> e instanceof ChestBlockEntity)
                    .map(e -> ((Container) e)).toList();

      AggregateItemStack sourceStock = calculateStock(sourceChests);
      AggregateItemStack destinationStock = calculateStock(destinationChests);

      PendingShipment.StockInfo stockInfo = new PendingShipment.StockInfo(destinationChests, sourceChests, destinationStock, sourceStock);

      int amountToShip = getItemCountForNextShipment(sourceStock, destinationStock);
      boolean shouldShip = shouldShip(sourceStock, destinationStock) && amountToShip > 0;

      return new PendingShipment(itemSearch, amountToShip, shouldShip, stockInfo);
   }

   public boolean takeShipment(
         CivilizedVillager villager,
         PendingShipment pendingShipment) {

      int quota = pendingShipment.getAmount();

      for (Container source : pendingShipment.getStock().getSourceChests()) {
         int transferred =
               ContainerHelper.transferNicely(
                     source,
                     villager.getLogisticsInventory(),
                     itemSearch,
                     quota);

         quota -= transferred;

         if (quota <= 0)
            break;
      }

      return quota < pendingShipment.getAmount();
   }

   public boolean returnShipment(
           CivilizedVillager villager,
           PendingShipment pendingShipment) {

      int quota = pendingShipment.getAmount();

      for (Container source : pendingShipment.getStock().getDestinationChests()) {
         int transferred =
                 ContainerHelper.transferNicely(
                         source,
                         villager.getLogisticsInventory(),
                         itemSearch,
                         quota);

         quota -= transferred;

         if (quota <= 0)
            break;
      }

      return quota < pendingShipment.getAmount();
   }

   protected AggregateItemStack calculateStock(Collection<Container> containers) {
      AggregateItemStack stock = new AggregateItemStack();
      containers.forEach(c -> {
         for (int i = 0; i < c.getContainerSize(); i++) {
            ItemStack itemStack = c.getItem(i);
            if (itemStack.isEmpty())
               continue;

            if (!itemSearch.test(itemStack))
               continue;

            stock.add(itemStack);
         }
      });

      return stock;
   }

   public enum Origin {
      AUTOMATIC,
      PLAYER_CREATED
   }
}
