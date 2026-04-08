package com.uncreated.civilized.item.events;

import java.util.HashMap;
import java.util.Map;

import com.uncreated.civilized.CivilizedMod;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(value = Dist.CLIENT, modid = CivilizedMod.CIVILIZED_MOD_ID)
public class ClientEquipmentEvents {

   private static ItemStack mainHandEquipped = ItemStack.EMPTY;
   private static ItemStack offHandEquipped = ItemStack.EMPTY;

   private static Map<EquipmentSlot, ItemStack> equippedItems = new HashMap<>();

   @SubscribeEvent
   public static void onPlayerTick(PlayerTickEvent.Pre event) {

      if (!event.getEntity().level().isClientSide())
         return;

      Player player = event.getEntity();

      Map<EquipmentSlot, EquipmentChange> changes = new HashMap<>();
      for (EquipmentSlot slot : EquipmentSlot.values()) {

         ItemStack currentlyEquipped = equippedItems.getOrDefault(slot, ItemStack.EMPTY);
         ItemStack equipped = player.getItemBySlot(slot);
         if (!equipped.equals(currentlyEquipped)) {
            changes.put(slot, new EquipmentChange(slot, currentlyEquipped, equipped));
         }

         equippedItems.put(slot, equipped);
      }

      if (!changes.isEmpty())
         NeoForge.EVENT_BUS.post(new PlayerChangedEquipment(player, changes));
   }
}
