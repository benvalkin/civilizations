package com.uncreated.civilized.entity.pathfinding;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.PathFinder;

/**
 * This class is a simple extension of the vanilla {@link GroundPathNavigation} that uses the custom
 * {@link VillagerWalkNodeEvaluator}.
 */
public class VillagerGroundPathNavigation extends GroundPathNavigation {
   public VillagerGroundPathNavigation(Mob mob, Level level) {
      super(mob, level);
   }

   @Override
   protected PathFinder createPathFinder(int p_26453_) {
      this.nodeEvaluator = new VillagerWalkNodeEvaluator();
      return new PathFinder(this.nodeEvaluator, p_26453_);
   }
}
