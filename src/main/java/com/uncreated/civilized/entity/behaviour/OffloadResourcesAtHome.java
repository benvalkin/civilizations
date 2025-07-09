package com.uncreated.civilized.entity.behaviour;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;

import com.uncreated.civilized.core.building.Building;
import com.uncreated.civilized.core.building.ServerBuildingsStore;
import com.uncreated.civilized.entity.CivilizedVillager;
import com.uncreated.civilized.neoforge.registration.ai.AIRegistry;
import com.uncreated.civilized.util.ContainerHelper;
import com.google.common.collect.ImmutableMap;
import com.mojang.logging.LogUtils;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.ChestBlockEntity;

public class OffloadResourcesAtHome extends Behavior<CivilizedVillager> {

   private static final Logger LOGGER = LogUtils.getLogger();

   private Building storehouse;
   private Building home;
   private List<ChestBlockEntity> chestsAtHome;

   public OffloadResourcesAtHome() {
      super(
            ImmutableMap.of(
                  MemoryModuleType.LOOK_TARGET,
                  MemoryStatus.VALUE_ABSENT,
                  MemoryModuleType.WALK_TARGET,
                  MemoryStatus.VALUE_ABSENT,
                  AIRegistry.MM_CAN_OFFLOAD.get(),
                  MemoryStatus.VALUE_PRESENT),
            20 * 60 * 3);
   }

   @Override
   protected boolean checkExtraStartConditions(ServerLevel level, CivilizedVillager villager) {

      Optional<Building> home = ServerBuildingsStore.INSTANCE.find(villager.getInfo().getHomeBuildingId());
      if (home.isEmpty()) {
         return false;
      }

      List<ChestBlockEntity> chestAtHome =
            home.get()
                  .getBlockEntitiesInside()
                  .stream()
                  .filter(b -> b instanceof ChestBlockEntity)
                  .map(b -> (ChestBlockEntity) b)
                  .toList();

      if (chestAtHome.isEmpty()) {
         return false;
      }

      this.home = home.get();
      this.chestsAtHome = chestAtHome;
      return true;
   }

   @Override
   protected void start(ServerLevel level, CivilizedVillager villager, long gameTime) {
      LOGGER.info("Villager going to offload resources.");
      offloaded = false;
   }

   @Override
   protected void stop(ServerLevel level, CivilizedVillager entity, long gameTime) {
      LOGGER.info("Villager stopped offloading resources.");
   }

   @Override
   protected boolean canStillUse(ServerLevel level, CivilizedVillager entity, long gameTime) {
      return !offloaded;
   }

   private boolean offloaded = false;

   @Override
   protected void tick(ServerLevel level, CivilizedVillager villager, long tickTime) {

      if (offloaded)
         return;

      BlockPos chestPos = chestsAtHome.getFirst().getBlockPos();
      // try walk to first chest
      if (!villager.blockPosition().closerThan(chestPos, 2)) {
         if (!villager.getBrain().hasMemoryValue(MemoryModuleType.WALK_TARGET)) {
            villager.getBrain().setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(chestPos, 0.4f, 1));
         }
         return;
      }

      for (int i = 0; i < villager.getInventory().getContainerSize(); i++) {

         ItemStack item = villager.getInventory().getItem(i);

         for (var chest : chestsAtHome) {
            // try to add item to chest
            ItemStack remainder = ContainerHelper.addItemNicely(chest, item);
            villager.getInventory().removeItem(i, item.getCount() - remainder.getCount());

            // if there is no remainder, we successfully inserted the stack
            if (remainder.isEmpty())
               break;
         }
      }

      offloaded = true;
      villager.getBrain().eraseMemory(AIRegistry.MM_CAN_OFFLOAD.get());
   }
}

// public class OffloadResources {
// public static OneShot<CivilizedVillager> create(
// MemoryModuleType<GlobalPos> poiPosMemory,
// float speedModifier,
// int maxDistFromPoi) {
// MutableLong mutablelong = new MutableLong(0L);
// return BehaviorBuilder.create(
// (villager) -> villager
// .group(villager.registered(MemoryModuleType.WALK_TARGET), villager.present(poiPosMemory))
// .apply(villager, (walkTarget, targetPos) -> (serverLevel, v, gameTicks) -> {
// GlobalPos globalpos = (GlobalPos) villager.get(targetPos);
// if (serverLevel.dimension() == globalpos.dimension()
// && globalpos.pos().closerToCenterThan(v.position(), (double) maxDistFromPoi)) {
// if (gameTicks <= mutablelong.getValue()) {
// return true;
// } else {
// Optional<Vec3> optional = Optional.ofNullable(LandRandomPos.getPos(v, 8, 6));
// walkTarget
// .setOrErase(optional.map((p_258816_) -> new WalkTarget(p_258816_, speedModifier, 1)));
// mutablelong.setValue(gameTicks + 180L);
// return true;
// }
// } else {
// return false;
// }
// }));
// }
// }
