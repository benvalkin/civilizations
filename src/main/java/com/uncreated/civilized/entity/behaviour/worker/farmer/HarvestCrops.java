package com.uncreated.civilized.entity.behaviour.worker.farmer;

import java.util.List;
import java.util.Optional;

import javax.annotation.Nullable;

import org.slf4j.Logger;

import com.google.common.collect.ImmutableMap;
import com.mojang.logging.LogUtils;
import com.uncreated.civilized.core.building.Building;
import com.uncreated.civilized.core.building.ServerBuildingsStore;
import com.uncreated.civilized.entity.CivilizedVillager;
import com.uncreated.civilized.entity.behaviour.MediumDistanceTravelTask;
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
   private @Nullable BlockPos nextFarmland = null;
   private @Nullable BlockPos nextMaturesCropToHarvest = null;
   private MediumDistanceTravelTask travelHelper;
   private Building workSite;

   public HarvestCrops() {
      super(
            ImmutableMap.of(
                  MemoryModuleType.LOOK_TARGET,
                  MemoryStatus.VALUE_ABSENT,
                  MemoryModuleType.WALK_TARGET,
                  MemoryStatus.VALUE_ABSENT,
                  MemoryModuleType.JOB_SITE,
                  MemoryStatus.VALUE_PRESENT,
                  AIRegistry.MM_HAS_WORK_OUTPUT_RESOURCES.get(),
                  MemoryStatus.VALUE_ABSENT));
   }

   @Override
   protected boolean checkExtraStartConditions(ServerLevel level, CivilizedVillager villager) {
      if (!super.checkExtraStartConditions(level, villager))
         return false;

      workSite = ServerBuildingsStore.INSTANCE.get(villager.getInfo().getPrimaryWorksiteId());
      findFarmBlocks(level);
      return nextMaturesCropToHarvest != null;
   }

   @Override
   protected void start(ServerLevel level, CivilizedVillager villager, long gameTime) {
      super.start(level, villager, gameTime);
      workSite = ServerBuildingsStore.INSTANCE.get(villager.getInfo().getPrimaryWorksiteId());
      travelHelper = new MediumDistanceTravelTask(villager, workSite.getBlockPos(), 5);
      LOGGER.info("Villager started harvesting crops.");
   }

   @Override
   protected void stop(ServerLevel level, CivilizedVillager entity, long gameTime) {
      super.stop(level, entity, gameTime);
      LOGGER.info("Villager stopped harvesting crops.");
   }

   @Override
   protected boolean canStillUse(ServerLevel level, CivilizedVillager entity, long gameTime) {
      super.canStillUse(level, entity, gameTime);
      Optional<GlobalPos> optional = entity.getBrain().getMemory(MemoryModuleType.JOB_SITE);
      if (optional.isEmpty()) {
         LOGGER.info("Villager will stop working because they have no more job site.");
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

         findFarmBlocks(level);
         LOGGER.info("Villager found a crop to harvest.");

         if (nextMaturesCropToHarvest == null) {
            doStop(level, villager, tickTime);
            return;
         }

         BlockState cropState = level.getBlockState(nextMaturesCropToHarvest);
         if (!(cropState.getBlock() instanceof CropBlock cropBlock))
            return;

         villager.getBrain()
               .setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(nextMaturesCropToHarvest, 0.25f, 1));
         villager.getBrain().setMemory(MemoryModuleType.LOOK_TARGET, new BlockPosTracker(nextMaturesCropToHarvest));

         toolHits++;
         villager.swing(InteractionHand.MAIN_HAND, true);

         if (toolHits == 4) {
            List<ItemStack> drops =
                  getDrops(level.getBlockState(nextMaturesCropToHarvest), level, nextMaturesCropToHarvest);
            drops.forEach(i -> villager.getInventory().addItem(i));
            LOGGER.info("Villager's inventory now has: {}", villager.getInventory().getItems());

            level.setBlockAndUpdate(nextMaturesCropToHarvest, getCropReplantState(cropState, cropBlock));
            villager.playSound(SoundEvents.CROP_BREAK, 1.0f, 1.0f);
            toolHits = 0;

            villager.getBrain().setMemory(AIRegistry.MM_HAS_WORK_OUTPUT_RESOURCES.get(), true);
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

   private void findFarmBlocks(ServerLevel serverLevel) {
      nextFarmland = null;
      nextMaturesCropToHarvest = null;

      workSite.getBounds().traverseBlocksWithinTerminateYChecksIfCanSeeSky(b -> {

         if (isFarmland(b, serverLevel)) {
            nextFarmland = b.immutable();
         }
         BlockPos above = b.above().immutable();
         if (isMatureCrop(above, serverLevel)) {
            nextMaturesCropToHarvest = above;
         }
      }, serverLevel);
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
