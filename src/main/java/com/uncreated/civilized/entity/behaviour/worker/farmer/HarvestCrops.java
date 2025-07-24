package com.uncreated.civilized.entity.behaviour.worker.farmer;

import java.util.List;
import java.util.Optional;

import com.uncreated.civilized.entity.behaviour.MediumDistanceTravelTask;
import org.slf4j.Logger;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import com.uncreated.civilized.entity.CivilizedVillager;
import com.uncreated.civilized.entity.behaviour.worker.WorkTaskBehaviour;
import com.uncreated.civilized.neoforge.registration.ai.AIRegistry;

import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.ai.behavior.BlockPosTracker;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.state.BlockState;

public class HarvestCrops extends WorkTaskBehaviour {
   public static final Logger LOGGER = LogUtils.getLogger();
   private long lastWorkTime;
   private final List<BlockPos> farmland = Lists.newArrayList();
   private final List<BlockPos> maturesCrops = Lists.newArrayList();
   private BlockPos cropFieldCenter;
   private MediumDistanceTravelTask travelHelper;

   public HarvestCrops() {
      super(
            ImmutableMap.of(
                  MemoryModuleType.LOOK_TARGET,
                  MemoryStatus.VALUE_ABSENT,
                  MemoryModuleType.WALK_TARGET,
                  MemoryStatus.VALUE_ABSENT,
                  MemoryModuleType.JOB_SITE,
                  MemoryStatus.VALUE_PRESENT));
   }

   @Override
   protected boolean checkExtraStartConditions(ServerLevel level, CivilizedVillager villager) {

      Optional<GlobalPos> jobSiteBlockPos = villager.getBrain().getMemory(MemoryModuleType.JOB_SITE);
      if (jobSiteBlockPos.isEmpty() || !jobSiteBlockPos.get().pos().closerThan(villager.blockPosition(), 6)) {
         return false;
      }

      this.cropFieldCenter = jobSiteBlockPos.get().pos();

      findFarmland(level);
      return !maturesCrops.isEmpty();
   }

   @Override
   protected void start(ServerLevel level, CivilizedVillager villager, long gameTime) {
      super.start(level, villager, gameTime);
      travelHelper = new MediumDistanceTravelTask(villager, MemoryModuleType.JOB_SITE, 5);
      LOGGER.info("Villager started harvesting crops.");
   }

   @Override
   protected void stop(ServerLevel level, CivilizedVillager entity, long gameTime) {
      super.stop(level, entity, gameTime);
      LOGGER.info("Villager stopped harvesting crops.");
   }

   @Override
   protected boolean canStillUse(ServerLevel level, CivilizedVillager entity, long gameTime) {

      Optional<GlobalPos> optional = entity.getBrain().getMemory(MemoryModuleType.JOB_SITE);
      if (optional.isEmpty()) {
         LOGGER.info("Villager will stop working because they have no more job site.");
         return false;
      } else if (maturesCrops.isEmpty()) {
         LOGGER.info("Villager will stop working because there are no more crops to havest.");
         return false;
      }

      return true;
   }

   private int toolHits = 0;

   @Override
   protected void tick(ServerLevel level, CivilizedVillager villager, long tickTime) {

      if (!travelHelper.isJourneySuccessful()) {
         travelHelper.walkToPoi(tickTime);
         return;
      }

      if (tickTime - lastWorkTime > 25) {

         lastWorkTime = tickTime;

         findFarmland(level);
         LOGGER.info("Villager found {} crops to harvest.", maturesCrops.size());

         if (!maturesCrops.isEmpty()) {
            BlockPos cropPos = maturesCrops.getFirst();

            villager.getBrain().setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(cropPos, 0.25f, 1));
            villager.getBrain().setMemory(MemoryModuleType.LOOK_TARGET, new BlockPosTracker(cropPos));

            toolHits++;
            villager.swing(InteractionHand.MAIN_HAND, true);

            if (toolHits == 4) {
               BlockState cropState = level.getBlockState(cropPos);
               List<ItemStack> drops = getDrops(level.getBlockState(cropPos), level, cropPos);
               drops.forEach(i -> villager.getInventory().addItem(i));
               LOGGER.info("Villager's inventory now has: {}", villager.getInventory().getItems());

               level.setBlockAndUpdate(cropPos, getCropReplantState(cropState, (CropBlock) cropState.getBlock()));
               villager.playSound(SoundEvents.CROP_BREAK, 1.0f, 1.0f);
               toolHits = 0;

               villager.getBrain().setMemory(AIRegistry.MM_CAN_OFFLOAD.get(), true);
            }
         }
      }
   }

   private BlockState getCropReplantState(BlockState currentState, CropBlock cropBlock) {
      return currentState.setValue(CropBlock.AGE, cropBlock.getStateForAge(0).getValue(CropBlock.AGE));
   }

   private List<ItemStack> getDrops(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos) {
      Item replant = blockState.getCloneItemStack(serverLevel, blockPos, true).getItem();
      final boolean[] removedReplant = { false };

      List<ItemStack> drops = Block.getDrops(blockState, serverLevel, blockPos, null);
      drops.forEach(stack -> {
         if (!removedReplant[0] && stack.getItem() == replant) {
            stack.setCount(stack.getCount() - 1);
            removedReplant[0] = true;
         }
      });
      return drops;
   }

   private void findFarmland(ServerLevel serverLevel) {
      BlockPos.MutableBlockPos mutableBlockPos = cropFieldCenter.mutable();
      farmland.clear();
      maturesCrops.clear();
      for (int x = -4; x <= 4; x++) {
         for (int y = -1; y <= 1; y++) {
            for (int z = -4; z <= 4; z++) {
               mutableBlockPos.set(cropFieldCenter.getX() + x, cropFieldCenter.getY() + y, cropFieldCenter.getZ() + z);
               if (isMatureCrop(mutableBlockPos, serverLevel)) {
                  maturesCrops.add(mutableBlockPos.immutable());
               }
               if (isFarmland(mutableBlockPos.below(), serverLevel)) {
                  farmland.add(mutableBlockPos.immutable());
               }
            }
         }
      }
   }

   private boolean isFarmland(BlockPos blockPos, ServerLevel serverLevel) {
      BlockState farmland = serverLevel.getBlockState(blockPos);
      BlockState above = serverLevel.getBlockState(blockPos.above());
      return farmland.getBlock() instanceof FarmBlock && above.isAir();
   }

   private boolean isMatureCrop(BlockPos blockPos, ServerLevel serverLevel) {
      BlockState crop = serverLevel.getBlockState(blockPos);
      return crop.getBlock() instanceof CropBlock cropBlock && cropBlock.isMaxAge(crop);
   }
}
