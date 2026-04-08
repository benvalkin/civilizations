package com.uncreated.civilized.item.events;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

public record EquipmentChange(EquipmentSlot slot, ItemStack from, ItemStack to) {
}
