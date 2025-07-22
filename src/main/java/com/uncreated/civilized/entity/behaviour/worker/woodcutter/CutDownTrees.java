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
   private final List<BlockPos> logsToHarvest = Lists.newArrayList();
   private final List<BlockPos> leavesToHarvest = Lists.newArrayList();
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
      return !logsToHarvest.isEmpty(); // only start when there are logs to harvest (not leaves)
   }

   @Override
   protected void start(ServerLevel level, CivilizedVillager villager, long gameTime) {
   }

   @Override
   protected void stop(ServerLevel level, CivilizedVillager entity, long gameTime) {
   }

   @Override
   protected boolean canStillUse(ServerLevel level, CivilizedVillager entity, long gameTime) {

      Optional<GlobalPos> optional = entity.getBrain().getMemory(MemoryModuleType.JOB_SITE);
      if (optional.isEmpty()) {
         return false;
      } else if (logsToHarvest.isEmpty() && leavesToHarvest.isEmpty()) {
         return false;
      }

      return true;
   }

   private int toolHits = 0;

   @Override
   protected void tick(ServerLevel level, CivilizedVillager villager, long tickTime) {

      if (tickTime - lastWorkTime > 6) {

         lastWorkTime = tickTime;

         findBlocksToHarvest(level);

         Optional<BlockPos> blockPos = logsToHarvest.stream().findFirst();
         if (blockPos.isEmpty())
            blockPos = leavesToHarvest.stream().findFirst();;
         if (blockPos.isEmpty())
            return;

         BlockPos pos = blockPos.get();

         villager.getBrain().setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(pos, 0.25f, 3));
         villager.getBrain().setMemory(MemoryModuleType.LOOK_TARGET, new BlockPosTracker(pos));

         toolHits++;
         villager.swing(InteractionHand.MAIN_HAND, true);

         BlockState blockState = level.getBlockState(pos);
         int requiredToolHits = blockState.getTags().anyMatch(t -> t.equals(BlockTags.LOGS)) ? 14 : 4;

         if (toolHits == requiredToolHits) {
            List<ItemStack> drops = Block.getDrops(blockState, level, pos, null);
            drops.forEach(i -> villager.getInventory().addItem(i));

            level.destroyBlock(pos, false);
            toolHits = 0;

            villager.getBrain().setMemory(AIRegistry.MM_CAN_OFFLOAD.get(), true);
         }
      }
   }

   private void findBlocksToHarvest(ServerLevel serverLevel) {
      BlockPos.MutableBlockPos current = cropFieldCenter.mutable();
      logsToHarvest.clear();
      leavesToHarvest.clear();
      for (int x = -5; x <= 5; x++) {
         for (int z = -5; z <= 5; z++) {
            for (int y = -1; y <= 32; y++) {
               current.set(cropFieldCenter.getX() + x, cropFieldCenter.getY() + y, cropFieldCenter.getZ() + z);

               BlockState block = serverLevel.getBlockState(current);
               if (isLog(block)) {
                  logsToHarvest.add(current.immutable());
               } else if (isLeaves(block)) {
                  leavesToHarvest.add(current.immutable());
               }

               if (serverLevel.canSeeSky(current))
                  break;
            }
         }
      }
   }

   private boolean isLog(BlockState blockState) {
      return blockState.getTags().anyMatch(t -> t.equals(BlockTags.LOGS));
   }

   private boolean isLeaves(BlockState blockState) {
      return blockState.getTags().anyMatch(t -> t.equals(BlockTags.LEAVES));
   }
}
