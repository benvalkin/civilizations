package com.uncreated.civilized.core.building.production;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class RecipeProductionSystem {

   private final Map<Class<? extends RecipeProductionMachine<?>>, RecipeProductionMachine<?>> machines;

   public RecipeProductionSystem() {
      machines = new HashMap<>();
   }

   public <Machine extends RecipeProductionMachine<?>> void registerMachine(
         Class<? extends Machine> machineType,
         Machine machine) {

      if (machines.containsKey(machineType)) {
         throw new IllegalArgumentException("Recipe production machine already registered.");
      }

      machines.put(machineType, machine);
   }

   public <Machine extends RecipeProductionMachine<?>> Machine getMachine(Class<Machine> machineType) {
      RecipeProductionMachine<?> machineInstance = machines.get(machineType);
      if (machineInstance == null) {
         throw new IllegalArgumentException(
               "Recipe production machine " + machineType + " is not supported by this system.");
      }

      return machineType.cast(machineInstance);
   }

   public Collection<RecipeProductionMachine<?>> registeredMachines() {
      return machines.values();
   }
}
