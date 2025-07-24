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

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.BlockPosTracker;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.state.BlockState;

public class ReplantSaplings extends Behavior<CivilizedVillager> {
   public static final Logger LOGGER = LogUtils.getLogger();
   private long lastWorkTime;
   private final List<BlockPos> validPlantingBlocks = Lists.newArrayList();
   private Building workSite;

   public ReplantSaplings() {
      super(
            ImmutableMap.of(
                  MemoryModuleType.LOOK_TARGET,
                  MemoryStatus.VALUE_ABSENT,
                  MemoryModuleType.WALK_TARGET,
                  MemoryStatus.VALUE_ABSENT,
                  MemoryModuleType.JOB_SITE,
                  MemoryStatus.VALUE_PRESENT),
            20 * 30);
   }

   @Override
   protected boolean checkExtraStartConditions(ServerLevel level, CivilizedVillager villager) {

      Optional<GlobalPos> jobSiteBlockPos = villager.getBrain().getMemory(MemoryModuleType.JOB_SITE);
      if (jobSiteBlockPos.isEmpty() || !jobSiteBlockPos.get().pos().closerThan(villager.blockPosition(), 6)) {
         return false;
      }

      workSite = ServerBuildingsStore.INSTANCE.get(villager.getInfo().getPrimaryWorksiteId());

      findValidPlantingBlocks(level);
      return !validPlantingBlocks.isEmpty(); // only start when it is possible to replant saplings
   }

   @Override
   protected void start(ServerLevel level, CivilizedVillager villager, long gameTime) {
   }

   @Override
   protected void stop(ServerLevel level, CivilizedVillager villager, long gameTime) {
   }

   @Override
   protected boolean canStillUse(ServerLevel level, CivilizedVillager entity, long gameTime) {

      Optional<GlobalPos> optional = entity.getBrain().getMemory(MemoryModuleType.JOB_SITE);
      if (optional.isEmpty()) {
         return false;
      } else if (validPlantingBlocks.isEmpty()) {
         return false;
      }

      return true;
   }

   @Override
   protected void tick(ServerLevel level, CivilizedVillager villager, long tickTime) {

      if (tickTime - lastWorkTime > 15) {

         lastWorkTime = tickTime;

         findValidPlantingBlocks(level);

         Optional<BlockPos> blockPos = validPlantingBlocks.stream().findFirst();
         if (blockPos.isEmpty())
            return;

         BlockPos pos = blockPos.get();

         villager.getBrain().setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(pos, 0.25f, 3));
         villager.getBrain().setMemory(MemoryModuleType.LOOK_TARGET, new BlockPosTracker(pos));

         Optional<ItemStack> saplingStack = getSaplingsInInventory(villager);
         if (saplingStack.isEmpty())
            return;

         villager.swing(InteractionHand.MAIN_HAND, true);

         SaplingBlock saplingBlock = (SaplingBlock) Block.byItem(saplingStack.get().getItem());

         level.setBlockAndUpdate(pos, saplingBlock.defaultBlockState());
         villager.playSound(SoundEvents.CROP_BREAK, 1.0f, 1.0f);

         villager.getInventory().removeItemType(saplingStack.get().getItem(), 1);
      }
   }

   private void findValidPlantingBlocks(ServerLevel serverLevel) {

      BlockPos.MutableBlockPos current = workSite.getBlockPos().mutable();
      validPlantingBlocks.clear();

      BlockPos lowerCorner = workSite.getBounds().getLowerCorner();
      BlockPos upperCorner = workSite.getBounds().getUpperCorner();

      int plantingXZMargin = 3;
      for (int x = lowerCorner.getX() + plantingXZMargin; x <= upperCorner.getX() - plantingXZMargin; x++) {
         for (int z = lowerCorner.getZ() + plantingXZMargin; z <= upperCorner.getZ() - plantingXZMargin; z++) {
            for (int y = lowerCorner.getY(); y <= upperCorner.getY(); y++) {
               current.set(x, y, z);

               BlockState currentState = serverLevel.getBlockState(current);
               if (isValidPlantingBlock(currentState)) {
                  BlockPos above = current.move(Direction.UP);
                  BlockState aboveState = serverLevel.getBlockState(above);
                  if (!aboveState.isEmpty())
                     continue;

                  validPlantingBlocks.add(above);
                  return;
               }
            }
         }
      }
   }

   private boolean isValidPlantingBlock(BlockState blockState) {
      return blockState.getTags().anyMatch(t -> t.equals(BlockTags.DIRT));
   }

   private Optional<ItemStack> getSaplingsInInventory(CivilizedVillager villager) {
      return villager.getWorkInputInventory().getItems().stream().filter(f -> f.is(ItemTags.SAPLINGS)).findFirst();
   }
}
