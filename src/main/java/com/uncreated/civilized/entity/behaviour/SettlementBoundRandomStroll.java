package com.uncreated.civilized.entity.behaviour;

import java.util.Optional;

import com.uncreated.civilized.core.building.Building;
import com.uncreated.civilized.core.building.ServerBuildingsStore;
import com.uncreated.civilized.core.building.util.BuildingUtil;
import com.uncreated.civilized.entity.CivilizedVillager;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.behavior.OneShot;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.phys.Vec3;

public class SettlementBoundRandomStroll {
   private static final int MAX_XZ_DIST = 10;
   private static final int MAX_Y_DIST = 7;

   public static OneShot<CivilizedVillager> create(float speedModifier) {
      return create(speedModifier, 10, 7);
   }

   public static OneShot<CivilizedVillager> create(float speedModifier, int maxHorizontalDist, int maxVerticalDist) {
      return BehaviorBuilder.create(
            (instance) -> instance.group(instance.absent(MemoryModuleType.WALK_TARGET))
                  .apply(instance, (walkTarget) -> (serverLevel, villager, gameTime) -> {
                     BlockPos pos = villager.blockPosition();

                     Vec3 vec3;
                     if (villager.getInfo().getSettlementId() == null) {
                        // if villager has no settlement, try to make them stay near their home (usually an Inn)
                        Optional<Building> home =
                              ServerBuildingsStore.INSTANCE.find(villager.getInfo().getHomeBuildingId());
                        if (home.isPresent())
                           vec3 =
                                 DefaultRandomPos.getPosTowards(
                                       villager,
                                       maxHorizontalDist,
                                       maxVerticalDist,
                                       home.get().getBlockPos().getBottomCenter(),
                                       (float) Math.PI / 2F);
                        else // if no home available, wander without boundaries
                           vec3 = LandRandomPos.getPos(villager, maxHorizontalDist, maxVerticalDist);
                     } else {
                        Optional<Building> nearbyBuilding =
                              BuildingUtil.findAnyNearbyHome(
                                    villager.getInfo().getSettlementId(),
                                    pos,
                                    maxHorizontalDist,
                                    ServerBuildingsStore.INSTANCE);

                        if (nearbyBuilding.isPresent()) {
                           // if the villager is already nearby a building, let them wander normally
                           vec3 = LandRandomPos.getPos(villager, maxHorizontalDist, maxVerticalDist);
                        } else {
                           // otherwise, try to wander back to this villager's settlement if possible
                           Optional<Building> someBuildingInSettlement =
                                 BuildingUtil.findAnyBuilding(
                                       villager.getInfo().getSettlementId(),
                                       ServerBuildingsStore.INSTANCE);

                           if (someBuildingInSettlement.isPresent())
                              vec3 =
                                    DefaultRandomPos.getPosTowards(
                                          villager,
                                          maxHorizontalDist,
                                          maxVerticalDist,
                                          someBuildingInSettlement.get().getBlockPos().getBottomCenter(),
                                          (double) ((float) Math.PI / 2F));
                           else // if no home available, wander without boundaries
                              vec3 = LandRandomPos.getPos(villager, maxHorizontalDist, maxVerticalDist);
                        }
                     }

                     walkTarget.setOrErase(
                           Optional.ofNullable(vec3).map((p_258865_) -> new WalkTarget(p_258865_, speedModifier, 0)));
                     return true;
                  }));
   }
}
