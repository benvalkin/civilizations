package com.uncreated.civilized.core;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SetIndex<Gk, V> {
   protected Map<Gk, Set<V>> index = new HashMap<>();

   public void add(Gk groupKey, V obj) {
      index.compute(groupKey, (s, existing) -> {
         Set<V> data = existing == null ? new HashSet<>() : existing;
         data.add(obj);
         return data;
      });
   }

   public void remove(Gk groupKey, V obj) {
      index.computeIfPresent(groupKey, (s, data) -> {
         data.remove(obj);
         return data.isEmpty() ? null : data;
      });
   }

   public Set<V> getValues(Gk groupKey) {
      return index.getOrDefault(groupKey, Set.of());
   }
}
