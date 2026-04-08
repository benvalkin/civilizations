package com.uncreated.civilized.item.events;

import java.util.Map;
import java.util.Optional;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

/**
 * Event for when the client-side player changes equipment. Called on the client only. Support all vanilla equipment
 * slot changes.
 */
public class PlayerChangedEquipment extends PlayerEvent {

   private final Map<EquipmentSlot, EquipmentChange> equipmentChanges;

   public PlayerChangedEquipment(Player player, Map<EquipmentSlot, EquipmentChange> equipmentChanges) {
      super(player);
      this.equipmentChanges = equipmentChanges;
   }

   public Optional<EquipmentChange> getEquipmentChange(EquipmentSlot slot) {
      return Optional.ofNullable(equipmentChanges.get(slot));
   }
}
