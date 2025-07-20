package com.uncreated.civilized.entity.behaviour.worker;

import static com.uncreated.civilized.entity.behaviour.CivilizedVillagerActivities.getMinimalLookBehavior;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import com.uncreated.civilized.core.villagerinfo.VillagerOccupation;
import com.uncreated.civilized.entity.CivilizedVillager;
import com.uncreated.civilized.entity.behaviour.MediumDistanceTravelOnceOff;
import com.uncreated.civilized.entity.behaviour.OffloadResourcesAtHome;
import com.uncreated.civilized.entity.behaviour.UpdateActivityFromSchedule;
import com.uncreated.civilized.entity.behaviour.worker.farmer.HarvestCrops;
import com.uncreated.civilized.entity.behaviour.worker.woodcutter.CutDownTrees;

import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.behavior.RunOne;
import net.minecraft.world.entity.ai.behavior.StrollAroundPoi;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;

public class WorkActivities {

   public static ImmutableList<Pair<Integer, ? extends BehaviorControl<CivilizedVillager>>> getWorkPackage(
         VillagerOccupation occupation) {
      return switch (occupation) {
      case FARMER -> getFarmerWorkPackage();
      case WOODCUTTER -> getWoodcutterWorkPackage();
      default -> ImmutableList.of();
      };
   }

   public static ImmutableList<Pair<Integer, ? extends BehaviorControl<CivilizedVillager>>> getFarmerWorkPackage() {
      return ImmutableList.of(
            getMinimalLookBehavior(),
            Pair.of(
                  1,
                  new RunOne<>(
                        ImmutableList.of(
                              // go to work. closeEnoughDist should +1 more StrollAroundPoi's maxDistFromPoi.
                              Pair.of(
                                    MediumDistanceTravelOnceOff.create(MemoryModuleType.JOB_SITE, 0.4f, 5, 300, 1500),
                                    3),
                              Pair.of(new HarvestCrops(), 4),
                              Pair.of(new OffloadResourcesAtHome(), 5),
                              // if cannot perform main work tasks, stroll around the job site.
                              Pair.of(StrollAroundPoi.create(MemoryModuleType.JOB_SITE, 0.25F, 4), 6)))),
            Pair.of(99, UpdateActivityFromSchedule.create()));
   }

   public static ImmutableList<Pair<Integer, ? extends BehaviorControl<CivilizedVillager>>> getWoodcutterWorkPackage() {
      return ImmutableList.of(
            getMinimalLookBehavior(),
            Pair.of(
                  1,
                  new RunOne<>(
                        ImmutableList.of(
                              // go to work. closeEnoughDist should +1 more StrollAroundPoi's maxDistFromPoi.
                              Pair.of(
                                    MediumDistanceTravelOnceOff.create(MemoryModuleType.JOB_SITE, 0.4f, 5, 300, 1500),
                                    3),
                              Pair.of(
                                    new CutDownTrees(), 4),
                              Pair.of(new OffloadResourcesAtHome(), 5),
                              // if cannot perform main work tasks, stroll around the job site.
                              Pair.of(StrollAroundPoi.create(MemoryModuleType.JOB_SITE, 0.25F, 4), 6)))),
            Pair.of(99, UpdateActivityFromSchedule.create()));
   }
}
