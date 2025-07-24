package com.uncreated.civilized.entity.behaviour.worker;

import static com.uncreated.civilized.entity.behaviour.CivilizedVillagerActivities.getMinimalLookBehavior;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import com.uncreated.civilized.core.villagerinfo.VillagerOccupation;
import com.uncreated.civilized.entity.CivilizedVillager;
import com.uncreated.civilized.entity.behaviour.IdleStrollAroundSettlement;
import com.uncreated.civilized.entity.behaviour.IdleStrollAroundWorksite;
import com.uncreated.civilized.entity.behaviour.OffloadResourcesAtHome;
import com.uncreated.civilized.entity.behaviour.UpdateActivityFromSchedule;
import com.uncreated.civilized.entity.behaviour.worker.farmer.HarvestCrops;
import com.uncreated.civilized.entity.behaviour.worker.woodcutter.CutDownTrees;
import com.uncreated.civilized.entity.behaviour.worker.woodcutter.FetchRequiredResourcesFromHome;
import com.uncreated.civilized.entity.behaviour.worker.woodcutter.ReplantSaplings;

import net.minecraft.world.entity.ai.behavior.BehaviorControl;

public class WorkActivities {

   public static ImmutableList<Pair<Integer, ? extends BehaviorControl<CivilizedVillager>>> getWorkPackage(
         VillagerOccupation occupation) {
      return switch (occupation) {
      case FARMER -> getFarmerWorkPackage();
      case WOODCUTTER -> getWoodcutterWorkPackage();
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
                              Pair.of(new HarvestCrops(), 4),
                              Pair.of(new OffloadResourcesAtHome(), 5),
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
                              Pair.of(new FetchRequiredResourcesFromHome(), 20 * 30),
                              Pair.of(new CutDownTrees(), 20 * 30),
                              Pair.of(new ReplantSaplings(), 20 * 30),
                              Pair.of(new OffloadResourcesAtHome(), 20 * 30),
                              Pair.of(new IdleStrollAroundWorksite(5, 3, 0.25f), 20 * 30),
                              Pair.of(new IdleStrollAroundSettlement(5, 3, 0.25f), 20 * 30)))),
            Pair.of(99, UpdateActivityFromSchedule.create()));
   }
}
