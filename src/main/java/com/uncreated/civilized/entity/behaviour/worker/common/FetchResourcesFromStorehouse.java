package com.uncreated.civilized.entity.behaviour.worker.common;

import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

import com.uncreated.civilized.core.building.logistics.PendingShipment;
import com.uncreated.civilized.core.building.logistics.imports.ImportOrders;
import com.uncreated.civilized.core.settlement.ServerSettlementsStore;
import com.uncreated.civilized.core.settlement.Settlement;
import org.slf4j.Logger;

import com.google.common.collect.ImmutableMap;
import com.mojang.logging.LogUtils;
import com.uncreated.civilized.core.building.Building;
import com.uncreated.civilized.core.building.ServerBuildingsStore;
import com.uncreated.civilized.entity.CivilizedVillager;
import com.uncreated.civilized.entity.behaviour.MediumDistanceTravelTask;
import com.uncreated.civilized.entity.behaviour.worker.WorkTaskBehaviour;
import com.uncreated.civilized.neoforge.registration.ai.AIRegistry;
import com.uncreated.civilized.util.ContainerHelper;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.ChestBlockEntity;

public class FetchResourcesFromStorehouse extends WorkTaskBehaviour {

   private static final Logger LOGGER = LogUtils.getLogger();
   private final Predicate<ItemStack> itemSearch;

   private Building home;
   private Building storehouse;
   private MediumDistanceTravelTask travelHelper;
   private Collection<PendingShipment> pendingShipments;

   public FetchResourcesFromStorehouse(Predicate<ItemStack> itemSearch) {
      super(
            ImmutableMap.of(
                  MemoryModuleType.LOOK_TARGET,
                  MemoryStatus.VALUE_ABSENT,
                  MemoryModuleType.WALK_TARGET,
                  MemoryStatus.VALUE_ABSENT,
                  AIRegistry.MM_HOLDING_WORK_INPUT_RESOURCES.get(),
                  MemoryStatus.VALUE_ABSENT));
      this.itemSearch = itemSearch;
   }

   @Override
   protected boolean checkExtraStartConditions(ServerLevel level, CivilizedVillager villager) {

      home = ServerBuildingsStore.INSTANCE.find(villager.getInfo().getHomeBuildingId()).orElse(null);
      if (home == null)
         return false;

      Settlement settlement = ServerSettlementsStore.INSTANCE.get(villager.getInfo().getSettlementId());
      storehouse = ServerBuildingsStore.INSTANCE.findStorehouse().orElse(null);
      if (storehouse == null)
         return false;


      ImportOrders importOrders = settlement.getLogisticsManager().getImportOrders(home);

      pendingShipments = importOrders.all().stream().map(order -> order.getNextShipment(storehouse, home, level)).toList();

      if (pendingShipments.stream().noneMatch(s -> s.isShouldProceed()))

   }

   @Override
   protected void start(ServerLevel level, CivilizedVillager villager, long gameTime) {
      super.start(level, villager, gameTime);
      LOGGER.info("Villager going to fetch resources.");
      done = false;

      travelHelper = new MediumDistanceTravelTask(villager, villager.getBrain().getMemory(MemoryModuleType.HOME).orElseThrow().pos(), 2);
   }

   @Override
   protected void stop(ServerLevel level, CivilizedVillager villager, long gameTime) {
      super.stop(level, villager, gameTime);
      LOGGER.info("Villager stopped fetching resources.");
   }

   @Override
   protected boolean canStillUse(ServerLevel level, CivilizedVillager entity, long gameTime) {
      return !done;
   }

   private boolean done = false;

   @Override
   protected void tick(ServerLevel level, CivilizedVillager villager, long tickTime) {

      if (!travelHelper.isJourneySuccessful()) {
         travelHelper.walkToPoi(tickTime);
         return;
      }

      int transferQuota = 16;
      int successfullyTransfered = 0;

      for (var chest : chestsAtHome) {

         successfullyTransfered +=
               ContainerHelper.transferNicely(
                     chest,
                     villager.getWorkInputInventory(),
                     itemSearch,
                     transferQuota - successfullyTransfered);

         if (successfullyTransfered >= transferQuota)
            break;
      }

      if (successfullyTransfered > 0)
         villager.getBrain().setMemory(AIRegistry.MM_HOLDING_WORK_INPUT_RESOURCES.get(), true);
      else
         villager.getBrain().setMemory(AIRegistry.MM_HOLDING_WORK_INPUT_RESOURCES.get(), false);

      done = true;
   }

   private boolean homeChestsHaveResources(List<ChestBlockEntity> chestsAtHome) {
      for (var chest : chestsAtHome) {
         if (chest.hasAnyMatching(itemSearch))
            return true;
      }
      return false;
   }
}
