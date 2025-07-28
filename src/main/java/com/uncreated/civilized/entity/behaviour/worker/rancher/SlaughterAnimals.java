package com.uncreated.civilized.entity.behaviour.worker.rancher;

import java.util.List;

import org.slf4j.Logger;

import com.google.common.collect.ImmutableMap;
import com.mojang.logging.LogUtils;
import com.uncreated.civilized.core.building.Building;
import com.uncreated.civilized.core.building.ServerBuildingsStore;
import com.uncreated.civilized.entity.CivilizedVillager;
import com.uncreated.civilized.entity.behaviour.MediumDistanceTravelTask;
import com.uncreated.civilized.entity.behaviour.worker.WorkTaskBehaviour;
import com.uncreated.civilized.neoforge.registration.ai.AIRegistry;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.phys.AABB;

public class SlaughterAnimals<T extends Animal> extends WorkTaskBehaviour {
   public static final Logger LOGGER = LogUtils.getLogger();
   private long lastWorkTime;
   private Building workSite;
   private MediumDistanceTravelTask travelHelper;

   private final Class<T> animalMobType;

   public SlaughterAnimals(Class<T> animalMobType) {
      super(
            ImmutableMap.of(
                  MemoryModuleType.LOOK_TARGET,
                  MemoryStatus.VALUE_ABSENT,
                  MemoryModuleType.WALK_TARGET,
                  MemoryStatus.VALUE_ABSENT,
                  MemoryModuleType.JOB_SITE,
                  MemoryStatus.VALUE_PRESENT));
      this.animalMobType = animalMobType;
   }

   @Override
   protected boolean checkExtraStartConditions(ServerLevel level, CivilizedVillager villager) {
      workSite = ServerBuildingsStore.INSTANCE.get(villager.getInfo().getPrimaryWorksiteId());

      killableAnimals = getKillableAdultAnimals(level);
      if (killableAnimals.size() <= 4)
         return false;

      return true;
   }

   @Override
   protected void start(ServerLevel level, CivilizedVillager villager, long gameTime) {
      super.start(level, villager, gameTime);
      AABB tooCloseBounds = workSite.getBounds().getEncapsulatingAABB();
      AABB closeEnoughBounds = tooCloseBounds.inflate(4);

      travelHelper =
            new MediumDistanceTravelTask(
                  villager,
                  workSite.getBlockPos(),
                  (v, d, closEnough) -> closeEnoughBounds.contains(v.position()),
                  Math.max((int) closeEnoughBounds.getXsize() / 2, (int) closeEnoughBounds.getZsize() / 2));
   }

   @Override
   protected void stop(ServerLevel level, CivilizedVillager villager, long gameTime) {
      super.stop(level, villager, gameTime);
   }

   @Override
   protected boolean canStillUse(ServerLevel level, CivilizedVillager villager, long gameTime) {
      return villager.getBrain().checkMemory(MemoryModuleType.JOB_SITE, MemoryStatus.VALUE_PRESENT)
            && killableAnimals.size() > 4;
   }

   private List<Animal> killableAnimals;

   @Override
   protected void tick(ServerLevel level, CivilizedVillager villager, long gameTime) {

      if (!travelHelper.isJourneySuccessful()) {
         travelHelper.walkToPoi(gameTime);
         return;
      }

      if (gameTime - lastWorkTime > 20 * 3) {

         lastWorkTime = gameTime;

         killableAnimals = getKillableAdultAnimals(level);
         if (killableAnimals.size() <= 4)
            return;

         villager.swing(InteractionHand.MAIN_HAND, true);

         Animal toKill = killableAnimals.getFirst();

         toKill.kill(level);

         List<ItemEntity> droppedItems =
               level.getEntitiesOfClass(ItemEntity.class, workSite.getBounds().getEncapsulatingAABB());
         droppedItems.forEach(i -> {
            villager.getWorkOutputInventory().addItem(i.getItem());
            i.remove(Entity.RemovalReason.KILLED);
         });

         villager.getBrain().setMemory(AIRegistry.MM_CAN_OFFLOAD.get(), true);
      }
   }

   protected List<Animal> getKillableAdultAnimals(ServerLevel level) {
      return level.getEntitiesOfClass(
            Animal.class,
            workSite.getBounds().getEncapsulatingAABB(),
            a -> !a.isBaby() && animalMobType.isInstance(a) && !a.isInLove());
   }
}
