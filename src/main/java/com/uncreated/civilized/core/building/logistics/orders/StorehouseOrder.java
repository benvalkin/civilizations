package com.uncreated.civilized.core.building.logistics.orders;

import java.util.Collection;
import java.util.List;
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

@Getter
public abstract class StorehouseOrder extends LogisticsOrder {

   public StorehouseOrder(Level level, String key, Predicate<ItemStack> itemSearch, LogisticsOrder.Origin origin) {
      super(level, key, itemSearch, origin);
   }

   protected abstract boolean shouldShip(AggregateItemStack sourceStock, AggregateItemStack destinationStock);

   protected abstract int getItemCountForNextShipment(
         AggregateItemStack sourceStock,
         AggregateItemStack destinationStock);

   public PendingShipment getNextShipment(List<Container> sourceChests, List<Container> destinationChests) {

      AggregateItemStack sourceStock = calculateStock(sourceChests);
      AggregateItemStack destinationStock = calculateStock(destinationChests);

      PendingShipment.StockInfo stockInfo =
            new PendingShipment.StockInfo(destinationChests, sourceChests, destinationStock, sourceStock);

      int amountToShip = getItemCountForNextShipment(sourceStock, destinationStock);
      boolean shouldShip = shouldShip(sourceStock, destinationStock) && amountToShip > 0;

      return new PendingShipment(itemSearch, amountToShip, shouldShip, stockInfo);
   }

   public boolean takeShipment(CivilizedVillager villager, PendingShipment pendingShipment) {

      int quota = pendingShipment.amount();

      for (Container source : pendingShipment.stock().getSourceChests()) {
         int transferred = ContainerHelper.transferNicely(source, villager.getLogisticsInventory(), itemSearch, quota);

         quota -= transferred;

         if (quota <= 0)
            break;
      }

      return quota < pendingShipment.amount();
   }

   public boolean returnShipment(CivilizedVillager villager, PendingShipment pendingShipment) {

      int quota = pendingShipment.amount();

      for (Container source : pendingShipment.stock().getDestinationChests()) {
         int transferred = ContainerHelper.transferNicely(source, villager.getLogisticsInventory(), itemSearch, quota);

         quota -= transferred;

         if (quota <= 0)
            break;
      }

      return quota < pendingShipment.amount();
   }
}
