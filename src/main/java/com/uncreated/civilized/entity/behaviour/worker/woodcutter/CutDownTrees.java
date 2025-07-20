package com.uncreated.civilized.entity.behaviour.worker.woodcutter;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import com.uncreated.civilized.entity.CivilizedVillager;
import com.uncreated.civilized.neoforge.registration.ai.AIRegistry;

import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.BlockPosTracker;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class CutDownTrees extends Behavior<CivilizedVillager> {
   public static final Logger LOGGER = LogUtils.getLogger();
   private long lastWorkTime;
   private final List<BlockPos> blocksToHarvest = Lists.newArrayList();
   private BlockPos cropFieldCenter;

   public CutDownTrees() {
      super(
            ImmutableMap.of(
                  MemoryModuleType.LOOK_TARGET,
                  MemoryStatus.VALUE_ABSENT,
                  MemoryModuleType.WALK_TARGET,
                  MemoryStatus.VALUE_ABSENT,
                  MemoryModuleType.JOB_SITE,
                  MemoryStatus.VALUE_PRESENT),
            20 * 120);
   }

   @Override
   protected boolean checkExtraStartConditions(ServerLevel level, CivilizedVillager villager) {

      Optional<GlobalPos> jobSiteBlockPos = villager.getBrain().getMemory(MemoryModuleType.JOB_SITE);
      if (jobSiteBlockPos.isEmpty() || !jobSiteBlockPos.get().pos().closerThan(villager.blockPosition(), 6)) {
         return false;
      }

      this.cropFieldCenter = jobSiteBlockPos.get().pos();

      findBlocksToHarvest(level);
      return !blocksToHarvest.isEmpty();
   }

   @Override
   protected void start(ServerLevel level, CivilizedVillager villager, long gameTime) {
      LOGGER.info("Villager started cutting wood.");
   }

   @Override
   protected void stop(ServerLevel level, CivilizedVillager entity, long gameTime) {
      LOGGER.info("Villager stopped cutting wood.");
   }

   @Override
   protected boolean canStillUse(ServerLevel level, CivilizedVillager entity, long gameTime) {

      Optional<GlobalPos> optional = entity.getBrain().getMemory(MemoryModuleType.JOB_SITE);
      if (optional.isEmpty()) {
         LOGGER.info("Villager will stop working because they have no more job site.");
         return false;
      } else if (blocksToHarvest.isEmpty()) {
         LOGGER.info("Villager will stop working because there are no more crops to havest.");
         return false;
      }

      return true;
   }

   private int toolHits = 0;

   @Override
   protected void tick(ServerLevel level, CivilizedVillager villager, long tickTime) {

      if (tickTime - lastWorkTime > 5) {

         lastWorkTime = tickTime;

         findBlocksToHarvest(level);
         LOGGER.info("Villager found {} crops to harvest.", blocksToHarvest.size());

         if (!blocksToHarvest.isEmpty()) {
            BlockPos pos = blocksToHarvest.getFirst();

            villager.getBrain().setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(pos, 0.25f, 1));
            villager.getBrain().setMemory(MemoryModuleType.LOOK_TARGET, new BlockPosTracker(pos));

            toolHits++;
            villager.swing(InteractionHand.MAIN_HAND, true);

            BlockState blockState = level.getBlockState(pos);
            int requiredToolHits = blockState.getTags().anyMatch(t -> t.equals(BlockTags.LOGS)) ? 15 : 4;

            if (toolHits == requiredToolHits) {
               List<ItemStack> drops = Block.getDrops(blockState, level, pos, null);
               drops.forEach(i -> villager.getInventory().addItem(i));
               LOGGER.info("Villager's inventory now has: {}", villager.getInventory().getItems());

               level.destroyBlock(pos, false);
               toolHits = 0;

               villager.getBrain().setMemory(AIRegistry.MM_CAN_OFFLOAD.get(), true);
            }
         }
      }
   }

   private void findBlocksToHarvest(ServerLevel serverLevel) {
      BlockPos.MutableBlockPos current = cropFieldCenter.mutable();
      blocksToHarvest.clear();
      for (int x = -5; x <= 5; x++) {
         for (int z = -5; z <= 5; z++) {
            for (int y = -1; y <= 32; y++) {
               current.set(cropFieldCenter.getX() + x, cropFieldCenter.getY() + y, cropFieldCenter.getZ() + z);

               if (isLogOrLeaves(current.below(), serverLevel)) {
                  blocksToHarvest.add(current.immutable());
               }
               if (serverLevel.canSeeSky(current))
                  break;
            }
         }
      }
   }

   private boolean isLogOrLeaves(BlockPos blockPos, ServerLevel serverLevel) {
      BlockState block = serverLevel.getBlockState(blockPos);
      return block.getTags().anyMatch(t -> t.equals(BlockTags.LOGS) || t.equals(BlockTags.LEAVES));
   }
}
