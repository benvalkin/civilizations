package com.uncreated.civilized.core.building.logistics.orders;

import java.util.List;
import java.util.function.Predicate;

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

   private final int minShipmentSize;
   private final int maxShipmentSize;

   public StorehouseOrder(
         Level level,
         String key,
         Predicate<ItemStack> itemSearch,
         Origin origin,
         int minShipmentSize,
         int maxShipmentSize) {
      super(level, key, itemSearch, origin);
      this.minShipmentSize = minShipmentSize;
      this.maxShipmentSize = maxShipmentSize;
   }

   protected abstract int getDeficitAtDestination(AggregateItemStack sourceStock, AggregateItemStack destinationStock);

   public PendingShipment getNextShipment(List<Container> sourceChests, List<Container> destinationChests) {

      AggregateItemStack sourceStock = calculateStock(sourceChests);
      AggregateItemStack destinationStock = calculateStock(destinationChests);

      PendingShipment.StockInfo stockInfo =
            new PendingShipment.StockInfo(destinationChests, sourceChests, destinationStock, sourceStock);

      int destinationDeficit = getDeficitAtDestination(sourceStock, destinationStock);
      int destinationSurplus = -destinationDeficit;

      int amountToShip;
      if (destinationDeficit > 0 && sourceStock.getCount() >= minShipmentSize) {
         amountToShip = Math.clamp(maxShipmentSize, minShipmentSize, sourceStock.getCount());
      } else {
         amountToShip = 0;
      }

      boolean shouldShip = amountToShip > 0;

      return new PendingShipment(
            itemSearch,
            amountToShip,
            shouldShip,
            Math.max(destinationDeficit, 0),
            Math.max(destinationSurplus, 0),
            stockInfo);
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
         int transferred = ContainerHelper.transferNicely(villager.getLogisticsInventory(), source, itemSearch, quota);

         quota -= transferred;

         if (quota <= 0)
            break;
      }

      return quota < pendingShipment.amount();
   }
}
