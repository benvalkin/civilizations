package com.uncreated.civilized.core.building.behaviour;

import java.util.List;
import java.util.PriorityQueue;
import java.util.Random;

import com.uncreated.civilized.core.StoreOperation;
import com.uncreated.civilized.core.building.Building;
import com.uncreated.civilized.core.building.util.BuildingUtil;
import com.uncreated.civilized.core.villagerinfo.ServerVillagerStore;
import com.uncreated.civilized.core.villagerinfo.VillagerInfo;
import com.uncreated.civilized.entity.CivilizedVillager;
import com.uncreated.civilized.core.villagerinfo.VillagerOccupation;
import com.uncreated.civilized.neoforge.registration.entity.EntityRegistry;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntitySpawnReason;

public class TradingPostBehaviour extends BuildingBehaviour {

   private Random random = new Random();

   protected TradingPostBehaviour(Building building) {
      super(building);
   }

   private static final int EVENTS_PER_DAY = 3;
   private static final int EVENT_DAY_TIME_START = 0;
   private static final int EVENT_DAY_TIME_CUTOFF = 14000;
   private static final float TRAVELLER_SPAWN_CHANCE = 0.4f;

   private final PriorityQueue<Long> eventQueue = new PriorityQueue<>(EVENTS_PER_DAY);
   boolean needNewEvents = true;

   @Override
   public void serverTick(ServerLevel level, long gameTime) {

      long todayTime = level.getDayTime() % 24000;

      if (todayTime >= EVENT_DAY_TIME_START && todayTime <= EVENT_DAY_TIME_CUTOFF) {
         if (needNewEvents) {
            eventQueue.clear();
            for (int i = 0; i < EVENTS_PER_DAY; i++)
               eventQueue.add(todayTime + random.nextLong(EVENT_DAY_TIME_CUTOFF - todayTime));

            LOGGER.debug(
                  "Queued {} new events at the respective times: {}",
                  eventQueue.size(),
                  String.join(", ", eventQueue.stream().map(String::valueOf).toList()));

            needNewEvents = false;
         }
         if (!eventQueue.isEmpty() && todayTime >= eventQueue.peek()) {
            Long eventDayTime = eventQueue.remove();

            LOGGER.debug("Event (which was scheduled for {}) was triggered at {}", eventDayTime, todayTime);
            // event has been triggered

            if (random.nextFloat() <= TRAVELLER_SPAWN_CHANCE)
               trySpawnTraveller(level);
         }
      } else if (!needNewEvents) {
         // event window closes at the end of the day
         // mark that we need to queue new events when the day is done
         needNewEvents = true;
         eventQueue.clear();
         LOGGER.debug("Event window has closed for today.");
      }
   }

   private void trySpawnTraveller(ServerLevel level) {

      List<VillagerInfo> occupants = BuildingUtil.getOccupants(building, ServerVillagerStore.INSTANCE);
      if (occupants.size() >= 4)
         return;

      CivilizedVillager villager =
            EntityRegistry.CIVILIZED_VILLAGER.get().spawn(level, building.getBlockPos(), EntitySpawnReason.EVENT);

      if (villager == null) {
         LOGGER.error(
               "Tried to spawn traveller villager at a Trading Post, but something went wrong during the entity spawning process.");
         return;
      }

      villager.getInfo().setHomeBuildingId(building.getBuildingId());
      villager.getInfo().setOccupation(VillagerOccupation.TRAVELLER);
      ServerVillagerStore.INSTANCE.setDirty();
      ServerVillagerStore.INSTANCE.replicateChange(villager.getInfo(), StoreOperation.UPDATE);

      LOGGER.debug("Villager spawned at Trading Post: {}", villager.getUUID());
   }
}
