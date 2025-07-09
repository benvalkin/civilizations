package com.uncreated.civilized.block.building.entity;

import org.jetbrains.annotations.NotNull;

import com.uncreated.civilized.core.building.BuildingType;
import com.uncreated.civilized.neoforge.registration.BlockRegistry;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.state.BlockState;

public class CropFarmBlockEntity extends AWorksiteContainerBlockEntity {

   public CropFarmBlockEntity(BlockPos pos, BlockState blockState) {
      super(BlockRegistry.CROP_FARM_BLOCK_ENTITY.get(), pos, blockState);
   }

   @Override
   public @NotNull BuildingType getBuildingType() {
      return BuildingType.CROP_FARM;
   }

   @Override
   protected Component getDefaultName() {
      return Component.literal("Farmer's House");
   }
}
