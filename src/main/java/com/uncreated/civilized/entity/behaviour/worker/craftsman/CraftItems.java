package com.uncreated.civilized.entity.behaviour.worker.craftsman;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import javax.annotation.Nullable;

import org.slf4j.Logger;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.uncreated.civilized.core.building.Building;
import com.uncreated.civilized.core.building.ServerBuildingsStore;
import com.uncreated.civilized.core.building.crafting.CraftingMachine;
import com.uncreated.civilized.core.building.crafting.PendingProductionOutput;
import com.uncreated.civilized.core.building.crafting.orders.ProductionOrder;
import com.uncreated.civilized.core.building.entity.LoadedBuilding;
import com.uncreated.civilized.core.building.entity.LoadedBuildings;
import com.uncreated.civilized.core.building.logistics.LogisticsManager;
import com.uncreated.civilized.core.building.logistics.orders.LogisticsOrder;
import com.uncreated.civilized.core.building.logistics.orders.imports.ImportOrder;
import com.uncreated.civilized.core.settlement.entity.LoadedSettlement;
import com.uncreated.civilized.core.settlement.entity.LoadedSettlements;
import com.uncreated.civilized.entity.CivilizedVillager;
import com.uncreated.civilized.entity.behaviour.MediumDistanceTravelTask;
import com.uncreated.civilized.entity.behaviour.worker.WorkTaskBehaviour;
import com.uncreated.civilized.neoforge.registration.ai.AIRegistry;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.behavior.BlockPosTracker;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class CraftItems extends WorkTaskBehaviour {
   public static final Logger LOGGER = LogUtils.getLogger();
   private final Predicate<Block> findWorkBlock;
   private long lastWorkTime;
   private LoadedBuilding home;
   @Nullable
   Building storehouse;
   private BlockPos workBlock;
   private MediumDistanceTravelTask travelHelper;

   int workSpeedMultiplier = 2;
   private CraftingMachine craftingMachine;
   private List<Container> ingredientsChests = new ArrayList<>();
   private List<Container> stockChests = new ArrayList<>();

   public CraftItems(Predicate<Block> findWorkBlock) {
      super(
            ImmutableMap.of(
                  MemoryModuleType.LOOK_TARGET,
                  MemoryStatus.VALUE_ABSENT,
                  MemoryModuleType.WALK_TARGET,
                  MemoryStatus.VALUE_ABSENT,
                  MemoryModuleType.HOME,
                  MemoryStatus.VALUE_PRESENT),
            20 * 60 * 4,
            20 * 60 * 4);
      this.findWorkBlock = findWorkBlock;
   }

   @Override
   protected boolean checkExtraStartConditions(ServerLevel level, CivilizedVillager villager) {

      Optional<LoadedBuilding> loadedHome = LoadedBuildings.checkLoaded(villager.getInfo().getHomeBuildingId());
      if (loadedHome.isEmpty())
         return false;

      home = loadedHome.get();

      Optional<LoadedSettlement> loadedSettlement = LoadedSettlements.checkLoaded(home.getBuilding().getSettlementId());
      if (loadedSettlement.isEmpty())
         return false;

      storehouse =
            ServerBuildingsStore.INSTANCE.findStorehouse(loadedSettlement.get().getSettlement().getSettlementId())
                  .orElse(null);

      home.getBuilding().getBounds().traverseBlocksWithin(traversal -> {
         BlockState blockState = level.getBlockState(traversal.getCurrentBlockPos());

         if (findWorkBlock.test(blockState.getBlock())) {
            workBlock = traversal.getCurrentBlockPos();
            traversal.terminate();
         }

         if (level.canSeeSky(traversal.getCurrentBlockPos()))
            traversal.skipToNextXZ();
      });

      if (workBlock == null)
         return false;

      LogisticsManager logisticsManager = loadedSettlement.get().getBehaviour().getLogisticsManager();

      craftingMachine = loadedHome.get().getBehaviour().getCraftingMachine();

      ingredientsChests = LogisticsOrder.findChests(level, home.getBuilding());
      stockChests =
            storehouse != null ? LogisticsOrder.findChests(level, home.getBuilding(), storehouse) : ingredientsChests;

      for (ProductionOrder productionOrder : craftingMachine.getOrders()) {
         List<ImportOrder> importOrders = productionOrder.createImportOrdersForIngredients(stockChests);
         importOrders.forEach(i -> logisticsManager.registerOrder(home.getBuilding(), i));
      }

      if (tryGetNextProductionOrder().isEmpty()) {
         // if we cannot craft right now, we should try do a logistics run instead
         villager.getBrain().setMemory(AIRegistry.MM_IMPORT_DESIRED.get(), true);

         return false;
      }

      return true;
   }

   @Override
   protected void start(ServerLevel level, CivilizedVillager villager, long gameTime) {
      super.start(level, villager, gameTime);
      travelHelper = new MediumDistanceTravelTask(villager, workBlock, 2);
   }

   @Override
   protected void stop(ServerLevel level, CivilizedVillager villager, long gameTime) {
      super.stop(level, villager, gameTime);
      villager.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
   }

   @Override
   protected boolean canStillUse(ServerLevel level, CivilizedVillager villager, long gameTime) {
      return villager.getBrain().checkMemory(MemoryModuleType.JOB_SITE, MemoryStatus.VALUE_PRESENT);
   }

   private int applyWorkSpeedMultiplier(int requiredToolHits) {
      return requiredToolHits / workSpeedMultiplier;
   }

   @Override
   protected void tick(ServerLevel level, CivilizedVillager villager, long gameTime) {

      if (!travelHelper.isJourneySuccessful()) {
         travelHelper.walkToPoi(gameTime);
         return;
      }

      if (gameTime - lastWorkTime > applyWorkSpeedMultiplier(30)) {

         lastWorkTime = gameTime;

         Optional<Pair<ProductionOrder, PendingProductionOutput>> nextOrder = tryGetNextProductionOrder();
         if (nextOrder.isEmpty()) {
            // nothing more to craft
            doStop(level, villager, gameTime);
            return;
         }

         PendingProductionOutput pendingOutput = nextOrder.get().getSecond();
         ItemStack resultItem = pendingOutput.resultItem();
         if (!villager.getWorkOutputInventory().canAddItem(resultItem)) {
            // nothing more to craft
            doStop(level, villager, gameTime);
            return;
         }

         villager.getWorkOutputInventory().addItem(resultItem);
         pendingOutput.consumeIngredients(level);

         villager.swing(InteractionHand.MAIN_HAND, true);
         villager.setItemSlot(EquipmentSlot.MAINHAND, resultItem.copyWithCount(1));
         villager.getBrain().setMemory(MemoryModuleType.LOOK_TARGET, new BlockPosTracker(workBlock));

         villager.getBrain().setMemory(AIRegistry.MM_HAS_WORK_OUTPUT_RESOURCES.get(), true);
      }
   }

   private Optional<Pair<ProductionOrder, PendingProductionOutput>> tryGetNextProductionOrder() {

      for (int i = 0; i < craftingMachine.getOrders().size(); i++) {
         ProductionOrder order = craftingMachine.getOrders().get(i);
         PendingProductionOutput pendingOutput = order.getNextOutput(ingredientsChests, stockChests);

         if (!pendingOutput.canProduce())
            continue;

         if (pendingOutput.stockDeficit() <= 0)
            continue;

         return Optional.of(Pair.of(order, pendingOutput));

      }
      return Optional.empty();
   }
}
