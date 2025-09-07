package com.uncreated.civilized.core.building.behaviour;

import com.uncreated.civilized.core.building.Building;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class GroveBehaviour extends BuildingBehaviour {

   public static final String FIELD_SAPLING = "sapling";

   private static final ItemStack DEFAULT_SAPLING = new ItemStack(Items.OAK_SAPLING);

   @Getter
   @Setter
   private ItemStack sapling;

   protected GroveBehaviour(Building building) {
      super(building);
      sapling = DEFAULT_SAPLING;
   }

   public void applyNbt(CompoundTag compoundTag) {
      read(compoundTag);
   }

   public CompoundTag toNbt() {
      CompoundTag tag = new CompoundTag();
      write(tag);
      return tag;
   }

   private void read(CompoundTag compoundTag) {
      String tagKey = FIELD_SAPLING;
      if (compoundTag.contains(tagKey))
         sapling = ItemStack.parseOptional(building.getRegistryAccess(), compoundTag.getCompound(tagKey));
      else
         sapling = DEFAULT_SAPLING;
   }

   private void write(CompoundTag tag) {
      if (sapling.isEmpty())
         sapling = DEFAULT_SAPLING;

      tag.put(FIELD_SAPLING, sapling.save(building.getRegistryAccess()));
   }

   public boolean isCorrectSapling(ItemStack stack) {
      return ItemStack.isSameItem(sapling, stack);
   }
}
