package com.uncreated.civilized.entity.behaviour.worker;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import com.mojang.datafixers.util.Pair;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.behavior.GateBehavior;
import net.minecraft.world.entity.ai.behavior.ShufflingList;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;

public class MGateBehavior<E extends LivingEntity> implements BehaviorControl<E> {
   private final Map<MemoryModuleType<?>, MemoryStatus> entryCondition;
   private final Set<MemoryModuleType<?>> exitErasedMemories;
   private final GateBehavior.OrderPolicy orderPolicy;
   private final GateBehavior.RunningPolicy runningPolicy;
   private final ShufflingList<BehaviorControl<? super E>> behaviors = new ShufflingList<>();
   private Behavior.Status status = Behavior.Status.STOPPED;

   public MGateBehavior(
         Map<MemoryModuleType<?>, MemoryStatus> entryCondition,
         Set<MemoryModuleType<?>> exitErasedMemories,
         GateBehavior.OrderPolicy orderPolicy,
         GateBehavior.RunningPolicy runningPolicy,
         List<Pair<? extends BehaviorControl<? super E>, Integer>> durations) {
      this.entryCondition = entryCondition;
      this.exitErasedMemories = exitErasedMemories;
      this.orderPolicy = orderPolicy;
      this.runningPolicy = runningPolicy;
      durations.forEach(
            p_258332_ -> this.behaviors.add((BehaviorControl<? super E>) p_258332_.getFirst(), p_258332_.getSecond()));
   }

   @Override
   public Behavior.Status getStatus() {
      return this.status;
   }

   private boolean hasRequiredMemories(E entity) {
      for (Entry<MemoryModuleType<?>, MemoryStatus> entry : this.entryCondition.entrySet()) {
         MemoryModuleType<?> memorymoduletype = entry.getKey();
         MemoryStatus memorystatus = entry.getValue();
         if (!entity.getBrain().checkMemory(memorymoduletype, memorystatus)) {
            return false;
         }
      }

      return true;
   }

   @Override
   public final boolean tryStart(ServerLevel p_259362_, E p_259746_, long p_259560_) {
      if (this.hasRequiredMemories(p_259746_)) {
         this.status = Behavior.Status.RUNNING;
         this.orderPolicy.apply(this.behaviors);
         this.runningPolicy.apply(this.behaviors.stream(), p_259362_, p_259746_, p_259560_);
         return true;
      } else {
         return false;
      }
   }

   @Override
   public final void tickOrStop(ServerLevel p_259934_, E p_259790_, long p_260259_) {
      this.behaviors.stream()
            .filter(p_258342_ -> p_258342_.getStatus() == Behavior.Status.RUNNING)
            .forEach(p_258336_ -> p_258336_.tickOrStop(p_259934_, p_259790_, p_260259_));
      if (this.behaviors.stream().noneMatch(p_258344_ -> p_258344_.getStatus() == Behavior.Status.RUNNING)) {
         this.doStop(p_259934_, p_259790_, p_260259_);
      }
   }

   @Override
   public final void doStop(ServerLevel p_259962_, E p_260250_, long p_259847_) {
      this.status = Behavior.Status.STOPPED;
      this.behaviors.stream()
            .filter(p_258337_ -> p_258337_.getStatus() == Behavior.Status.RUNNING)
            .forEach(p_258341_ -> p_258341_.doStop(p_259962_, p_260250_, p_259847_));
      this.exitErasedMemories.forEach(p_260250_.getBrain()::eraseMemory);
   }

   @Override
   public String debugString() {

      List<String> behaviourNames =
            this.behaviors.stream()
                  .filter(b -> b.getStatus() == Behavior.Status.RUNNING)
                  .map(BehaviorControl::debugString)
                  .toList();

      return String.join(" | ", behaviourNames);
   }

   @Override
   public String toString() {
      Set<? extends BehaviorControl<? super E>> set =
            this.behaviors.stream()
                  .filter(p_258343_ -> p_258343_.getStatus() == Behavior.Status.RUNNING)
                  .collect(Collectors.toSet());
      return "(" + this.getClass().getSimpleName() + "): " + set;
   }
}
