package com.uncreated.civilized.entity.behaviour.worker.miner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.AABB;

public class MineOres extends WorkTaskBehaviour {
   public static final Logger LOGGER = LogUtils.getLogger();
   private long lastWorkTime;
   private Building workSite;
   private MediumDistanceTravelTask travelHelper;

   int workSpeedMultiplier = 2;
   private boolean foundOres = false;
   Map<BlockPos, Long> recentlyMinedBlocks;

   public MineOres() {
      super(
            ImmutableMap.of(
                  MemoryModuleType.LOOK_TARGET,
                  MemoryStatus.VALUE_ABSENT,
                  MemoryModuleType.WALK_TARGET,
                  MemoryStatus.VALUE_ABSENT,
                  MemoryModuleType.JOB_SITE,
                  MemoryStatus.VALUE_PRESENT,
                  AIRegistry.MM_CAN_OFFLOAD.get(),
                  MemoryStatus.VALUE_ABSENT),
            20 * 60 * 4,
            20 * 60 * 4);
      recentlyMinedBlocks = new HashMap<>();
   }

   @Override
   protected boolean checkExtraStartConditions(ServerLevel level, CivilizedVillager villager) {

      workSite = ServerBuildingsStore.INSTANCE.get(villager.getInfo().getPrimaryWorksiteId());
      return true; // todo: setting worksite can move to start method in all work tasks
   }

   @Override
   protected void start(ServerLevel level, CivilizedVillager villager, long gameTime) {
      super.start(level, villager, gameTime);
      foundOres = false;
      travelHelper = new MediumDistanceTravelTask(villager, MemoryModuleType.JOB_SITE, 2);
   }

   @Override
   protected void stop(ServerLevel level, CivilizedVillager villager, long gameTime) {
      super.stop(level, villager, gameTime);
      if (foundOres)
         villager.getBrain().setMemory(AIRegistry.MM_CAN_OFFLOAD.get(), true);
   }

   @Override
   protected boolean canStillUse(ServerLevel level, CivilizedVillager villager, long gameTime) {
      return villager.getBrain().checkMemory(MemoryModuleType.JOB_SITE, MemoryStatus.VALUE_PRESENT);
   }

   private int toolHits = 0;

   private int applyWorkSpeedMultiplier(int requiredToolHits) {
      return requiredToolHits / workSpeedMultiplier;
   }

   @Override
   protected void tick(ServerLevel level, CivilizedVillager villager, long gameTime) {

      if (!travelHelper.isJourneySuccessful()) {
         travelHelper.walkToPoi(gameTime);
         return;
      }

      if (gameTime - lastWorkTime > 30) {

         lastWorkTime = gameTime;

         toolHits++;
         villager.swing(InteractionHand.MAIN_HAND, true);

         int requiredToolHits = 3;

         if (toolHits >= applyWorkSpeedMultiplier(requiredToolHits)) {

            mineOreVein(level, villager, gameTime);

            toolHits = 0;
         }
      }
   }

   private void mineOreVein(ServerLevel level, CivilizedVillager villager, long gameTime) {
      int miningDepth = 0;

      LevelChunk chunk = level.getChunkAt(workSite.getBlockPos());
      int minX = chunk.getPos().getMinBlockX();
      int minZ = chunk.getPos().getMinBlockZ();
      int maxX = chunk.getPos().getMaxBlockX();
      int maxZ = chunk.getPos().getMaxBlockZ();
      int minY = Math.max(miningDepth, chunk.getMinY());
      int maxY = workSite.getBlockPos().getY();

      int mineX = villager.getRandom().nextInt(minX, maxX + 1);
      int mineY = villager.getRandom().nextInt(minY, maxY + 1);
      int mineZ = villager.getRandom().nextInt(minZ, maxZ + 1);

      BlockPos toMine = new BlockPos(mineX, mineY, mineZ);

      BlockState blockState = chunk.getBlockState(toMine);

      if (!(blockState.getBlock() instanceof DropExperienceBlock deb))
         return;

      Iterable<BlockPos> blocksToMine =
            BlockPos.betweenClosed(AABB.encapsulatingFullBlocks(toMine.offset(-1, -1, -1), toMine.offset(1, 1, 1)));
      blocksToMine.forEach(b -> mineBlock(b, villager, chunk, level, gameTime));

   }

   private void mineBlock(
         BlockPos toMine,
         CivilizedVillager villager,
         ChunkAccess chunk,
         ServerLevel level,
         long gameTime) {
      BlockState blockState = chunk.getBlockState(toMine);

      if (!(blockState.getBlock() instanceof DropExperienceBlock deb))
         return;

      Long timeLastMinedThisBlock = recentlyMinedBlocks.get(toMine);
      if (timeLastMinedThisBlock != null && gameTime - timeLastMinedThisBlock < 20 * 60 * 10)
         return;

      recentlyMinedBlocks.put(toMine, timeLastMinedThisBlock);

      List<ItemStack> drops = Block.getDrops(blockState, level, toMine, null);

      float minerEfficiency = 0.2f;

      for (int i = 0; i < drops.size(); i++) {
         ItemStack drop = drops.get(i);
         if (i > 0 && villager.getRandom().nextFloat() > minerEfficiency)
            continue;

         villager.getInventory().addItem(drop);
         foundOres = true;
      }
   }
}
