package com.uncreated.civilized.core.building.logistics.orders;

import com.uncreated.civilized.core.building.logistics.AggregateItemStack;
import lombok.Getter;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.function.Predicate;

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

   public void setExpiryTime(int expiryTimeMinutes) {
      this.expiryTimeMinutes = expiryTimeMinutes;
      this.startTime = Instant.now();
   }

   public boolean isExpired() {
      if (expiryTimeMinutes == null || startTime == null)
         return false;

      return Duration.between(startTime, Instant.now()).getSeconds() > expiryTimeMinutes * 60;
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
