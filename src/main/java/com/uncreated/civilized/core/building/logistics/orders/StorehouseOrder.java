package com.uncreated.civilized.core.building.logistics.orders;

import java.util.Collection;
import java.util.function.Predicate;

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
public abstract class StorehouseOrder extends LogisticsOrder {


   public StorehouseOrder(String key, Predicate<ItemStack> itemSearch, LogisticsOrder.Origin origin) {
      super(key, itemSearch, origin);
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

      int quota = pendingShipment.amount();

      for (Container source : pendingShipment.stock().getSourceChests()) {
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

      return quota < pendingShipment.amount();
   }

   public boolean returnShipment(
           CivilizedVillager villager,
           PendingShipment pendingShipment) {

      int quota = pendingShipment.amount();

      for (Container source : pendingShipment.stock().getDestinationChests()) {
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

      return quota < pendingShipment.amount();
   }
}
