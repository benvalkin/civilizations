package com.uncreated.civilized.core.building.logistics;

import lombok.Getter;
import net.minecraft.world.item.ItemStack;

@Getter
public class PendingShipment {
   private final ItemStack itemType;
   private final int amount;
   private final boolean shouldProceed;

   public PendingShipment(ItemStack itemType, int amount, boolean shouldProceed) {
      this.itemType = itemType;
      this.amount = amount;
      this.shouldProceed = shouldProceed;
   }
}
