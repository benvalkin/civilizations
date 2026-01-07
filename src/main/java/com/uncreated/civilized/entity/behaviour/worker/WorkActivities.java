package com.uncreated.civilized.entity.behaviour.worker;

import static com.uncreated.civilized.entity.behaviour.CivilizedVillagerActivities.getMinimalLookBehavior;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import com.uncreated.civilized.core.villagerinfo.VillagerOccupation;
import com.uncreated.civilized.entity.CivilizedVillager;
import com.uncreated.civilized.entity.behaviour.IdleStrollAroundSettlement;
import com.uncreated.civilized.entity.behaviour.UpdateActivityFromSchedule;
import com.uncreated.civilized.entity.behaviour.worker.common.IdleStrollAroundWorksite;
import com.uncreated.civilized.entity.behaviour.worker.common.IdleStrollOutsideWorksite;
import com.uncreated.civilized.entity.behaviour.worker.common.logistics.OffloadExportsAtStorehouse;
import com.uncreated.civilized.entity.behaviour.worker.common.logistics.OffloadImportsAtHome;
import com.uncreated.civilized.entity.behaviour.worker.common.logistics.OffloadWorkResourcesAtHome;
import com.uncreated.civilized.entity.behaviour.worker.common.logistics.PickupExportsAtHome;
import com.uncreated.civilized.entity.behaviour.worker.common.logistics.PickupImportsAtStorehouse;
import com.uncreated.civilized.entity.behaviour.worker.common.logistics.PickupWorkResourcesFromHome;
import com.uncreated.civilized.entity.behaviour.worker.craftsman.CraftItems;
import com.uncreated.civilized.entity.behaviour.worker.farmer.HarvestCrops;
import com.uncreated.civilized.entity.behaviour.worker.farmer.PlantCrops;
import com.uncreated.civilized.entity.behaviour.worker.miner.MineOres;
import com.uncreated.civilized.entity.behaviour.worker.rancher.BreedAnimals;
import com.uncreated.civilized.entity.behaviour.worker.rancher.SlaughterAnimals;
import com.uncreated.civilized.entity.behaviour.worker.woodcutter.CutDownTrees;
import com.uncreated.civilized.entity.behaviour.worker.woodcutter.ReplantSaplings;

import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.level.block.CraftingTableBlock;

public class WorkActivities {

   public static ImmutableList<Pair<Integer, ? extends BehaviorControl<CivilizedVillager>>> getWorkPackage(
         VillagerOccupation occupation) {
      return switch (occupation) {
      case FARMER -> getFarmerWorkPackage();
      case WOODCUTTER -> getWoodcutterWorkPackage();
      case MINER -> getMinerWorkPackage();
      case RANCHER -> getRancherWorkPackage();
      case BAKER -> getArtisanWorkPackage();
      default -> throw new IllegalStateException(
            String.format("Villager occupation '%s' does not support working behaviour.", occupation));
      };
   }

   public static ImmutableList<Pair<Integer, ? extends BehaviorControl<CivilizedVillager>>> getFarmerWorkPackage() {
      return ImmutableList.of(
            getMinimalLookBehavior(),
            Pair.of(
                  1,
                  new CoreWorkBehaviour(
                        ImmutableList.of(
                              Pair.of(new PickupExportsAtHome(), 20 * 30),
                              Pair.of(new OffloadExportsAtStorehouse(), 20 * 30),
                              Pair.of(new OffloadImportsAtHome(), 20 * 30),
                              Pair.of(new HarvestCrops(), 20 * 30),
                              Pair.of(new PlantCrops(), 20 * 30),
                              Pair.of(new OffloadWorkResourcesAtHome(), 20 * 30),
                              Pair.of(new IdleStrollAroundWorksite(5, 3, 0.25f), 20 * 30),
                              Pair.of(new IdleStrollAroundSettlement(5, 3, 0.25f), 20 * 30)))),
            Pair.of(99, UpdateActivityFromSchedule.create()));
   }

   public static ImmutableList<Pair<Integer, ? extends BehaviorControl<CivilizedVillager>>> getWoodcutterWorkPackage() {
      return ImmutableList.of(
            getMinimalLookBehavior(),
            Pair.of(
                  1,
                  new CoreWorkBehaviour(
                        ImmutableList.of(
                              Pair.of(new PickupExportsAtHome(), 20 * 30),
                              Pair.of(new OffloadExportsAtStorehouse(), 20 * 30),
                              Pair.of(new OffloadImportsAtHome(), 20 * 30),
                              Pair.of(new PickupWorkResourcesFromHome(), 20 * 30),
                              Pair.of(new CutDownTrees(), 20 * 30),
                              Pair.of(new ReplantSaplings(), 20 * 30),
                              Pair.of(new OffloadWorkResourcesAtHome(), 20 * 30),
                              Pair.of(new IdleStrollAroundWorksite(5, 3, 0.25f), 20 * 30),
                              Pair.of(new IdleStrollAroundSettlement(5, 3, 0.25f), 20 * 30)))),
            Pair.of(99, UpdateActivityFromSchedule.create()));
   }

   public static ImmutableList<Pair<Integer, ? extends BehaviorControl<CivilizedVillager>>> getMinerWorkPackage() {
      return ImmutableList.of(
            getMinimalLookBehavior(),
            Pair.of(
                  1,
                  new CoreWorkBehaviour(
                        ImmutableList.of(
                              Pair.of(new PickupExportsAtHome(), 20 * 30),
                              Pair.of(new OffloadExportsAtStorehouse(), 20 * 30),
                              Pair.of(new OffloadImportsAtHome(), 20 * 30),
                              Pair.of(new PickupWorkResourcesFromHome(), 20 * 30),
                              Pair.of(new MineOres(), 60),
                              Pair.of(new OffloadWorkResourcesAtHome(), 60),
                              Pair.of(new IdleStrollAroundWorksite(5, 3, 0.25f), 20 * 30),
                              Pair.of(new IdleStrollAroundSettlement(5, 3, 0.25f), 20 * 30)))),
            Pair.of(99, UpdateActivityFromSchedule.create()));
   }

   public static ImmutableList<Pair<Integer, ? extends BehaviorControl<CivilizedVillager>>> getRancherWorkPackage() {
      return ImmutableList.of(
            getMinimalLookBehavior(),
            Pair.of(
                  1,
                  new CoreWorkBehaviour(
                        ImmutableList.of(
                              Pair.of(new PickupExportsAtHome(), 20 * 30),
                              Pair.of(new OffloadExportsAtStorehouse(), 20 * 30),
                              Pair.of(new OffloadImportsAtHome(), 20 * 30),
                              Pair.of(new PickupWorkResourcesFromHome(), 20 * 30),
                              Pair.of(new BreedAnimals<>(Cow.class), 20 * 30),
                              Pair.of(new SlaughterAnimals<>(Cow.class), 20 * 30),
                              Pair.of(new OffloadWorkResourcesAtHome(), 20 * 30),
                              Pair.of(new IdleStrollOutsideWorksite(4, 3, 0.25f), 20 * 30),
                              Pair.of(new IdleStrollAroundSettlement(5, 3, 0.25f), 20 * 30)))),
            Pair.of(99, UpdateActivityFromSchedule.create()));
   }

   public static ImmutableList<Pair<Integer, ? extends BehaviorControl<CivilizedVillager>>> getArtisanWorkPackage() {
      return ImmutableList.of(
            getMinimalLookBehavior(),
            Pair.of(
                  1,
                  new CoreWorkBehaviour(
                        ImmutableList.of(
                              Pair.of(new CraftItems(b -> b instanceof CraftingTableBlock), 60),
                              Pair.of(new OffloadWorkResourcesAtHome(), 60),
                              Pair.of(new PickupExportsAtHome(), 20 * 30),
                              Pair.of(new OffloadExportsAtStorehouse(), 20 * 30),
                              Pair.of(new PickupImportsAtStorehouse(), 20 * 30),
                              Pair.of(new OffloadImportsAtHome(), 20 * 30),
                              Pair.of(new IdleStrollAroundWorksite(4, 3, 0.25f), 20 * 30),
                              Pair.of(new IdleStrollAroundSettlement(5, 3, 0.25f), 20 * 30)))),
            Pair.of(99, UpdateActivityFromSchedule.create()));
   }
}
