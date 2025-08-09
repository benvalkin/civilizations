package com.uncreated.civilized.entity.behaviour.worker.woodcutter;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
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
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.ai.behavior.BlockPosTracker;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class CutDownTrees extends WorkTaskBehaviour {
   public static final Logger LOGGER = LogUtils.getLogger();
   private long lastWorkTime;
   private final List<BlockPos> logsToHarvest = Lists.newArrayList();
   private final List<BlockPos> leavesToHarvest = Lists.newArrayList();
   private Building workSite;
   private MediumDistanceTravelTask travelHelper;

   int workSpeedMultiplier = 2;

   public CutDownTrees() {
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

      workSite = ServerBuildingsStore.INSTANCE.get(villager.getInfo().getPrimaryWorksiteId());

      findBlocksToHarvest(level);
      return !logsToHarvest.isEmpty(); // only start when there are logs to harvest (not leaves)
   }

   @Override
   protected void start(ServerLevel level, CivilizedVillager villager, long gameTime) {
      super.start(level, villager, gameTime);
      travelHelper = new MediumDistanceTravelTask(villager, workSite.getBlockPos(), 5);
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

   private int applyWorkSpeedMultiplier(int requiredToolHits) {
      return requiredToolHits / workSpeedMultiplier;
   }

   @Override
   protected void tick(ServerLevel level, CivilizedVillager villager, long tickTime) {

      if (!travelHelper.isJourneySuccessful()) {
         travelHelper.walkToPoi(tickTime);
         return;
      }

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

         if (toolHits >= applyWorkSpeedMultiplier(requiredToolHits)) {
            List<ItemStack> drops = Block.getDrops(blockState, level, pos, null);
            drops.forEach(i -> villager.getInventory().addItem(i));

            level.destroyBlock(pos, false);
            toolHits = 0;

            villager.getBrain().setMemory(AIRegistry.MM_HOLDING_WORK_OUTPUT_RESOURCES.get(), true);
         }
      }
   }

   private void findBlocksToHarvest(ServerLevel serverLevel) {
      BlockPos.MutableBlockPos current = workSite.getBlockPos().mutable();
      logsToHarvest.clear();
      leavesToHarvest.clear();

      BlockPos lowerCorner = workSite.getBounds().getLowerCorner();
      BlockPos upperCorner = workSite.getBounds().getUpperCorner();
      int yStart = workSite.getBounds().getCenter().getY() - 2;
      int yEnd = workSite.getBounds().getCenter().getY() + 40;

      for (int x = lowerCorner.getX(); x <= upperCorner.getX(); x++) {
         for (int z = lowerCorner.getZ(); z <= upperCorner.getZ(); z++) {
            for (int y = yStart; y <= yEnd; y++) {
               current.set(x, y, z);

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
