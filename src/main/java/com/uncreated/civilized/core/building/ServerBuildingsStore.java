package com.uncreated.civilized.core.building;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.uncreated.civilized.core.building.events.model.BuildingDeletedEvent;
import org.apache.commons.compress.utils.Lists;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;
import com.uncreated.civilized.core.StoreOperation;
import com.uncreated.civilized.core.building.bounds.BuildingBounds;
import com.uncreated.civilized.core.building.events.model.BuildingUpdatedEvent;

import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class ServerBuildingsStore extends BuildingStore {

   protected static final Logger LOGGER = LogUtils.getLogger();
   public static ServerBuildingsStore INSTANCE;

   @Getter
   private Level level;

   protected ServerBuildingsStore() {
      super();
   }

   public static void loadServer(MinecraftServer server) {
      INSTANCE =
            server.overworld()
                  .getDataStorage()
                  .computeIfAbsent(
                        new SavedData.Factory<>(ServerBuildingsStore::createDefault, ServerBuildingsStore::load),
                        STORAGE_FILE_NAME);

      INSTANCE.level = server.overworld();
   }

   public static final String STORAGE_FILE_NAME = "civilized_buildings";

   // Create new instance of saved data
   private static ServerBuildingsStore createDefault() {
      return new ServerBuildingsStore();
   }

   public void onServerTick(MinecraftServer server, boolean hasTickTime) {
      for (Building building : buildings.values()) {

         if (building.getBehaviour() == null)
            continue;

         try {
            building.getBehaviour().serverTick(server.overworld().getLevel(), server.overworld().getGameTime());
         } catch (Exception ex) {
            LOGGER.error("Error while ticking building {}", building.getBuildingId(), ex);
         }
      }
   }

   @Override
   public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {

      ListTag tags = new ListTag();
      for (Building building : buildings.values()) {
         CompoundTag item = new CompoundTag();
         item.putUUID(Building.FIELD_BUILDING_ID, building.getBuildingId());
         item.putUUID(Building.FIELD_SETTLEMENT_ID, building.getSettlementId());
         item.putUUID(Building.FIELD_PLACER_ID, building.getPlacerId());
         item.putString(Building.FIELD_BUILDING_TYPE, building.getBuildingType().name());
         item.putLong(Building.FIELD_CENTER_POS, building.getBounds().getCenter().asLong());
         item.putLong(Building.FIELD_LOWER_CORNER_POS, building.getBounds().getLowerCorner().asLong());
         item.putLong(Building.FIELD_UPPER_CORNER_POS, building.getBounds().getUpperCorner().asLong());

         ListTag occupantIds = new ListTag();
         for (UUID occupantId : building.getOccupantIds()) {
            CompoundTag occupantTag = new CompoundTag();
            occupantTag.putUUID(Building.FIELD_LIST_ITEM_OCCUPANT_ID, occupantId);
            occupantIds.add(occupantTag);
         }
         item.put(Building.FIELD_LIST_OCCUPANTS, occupantIds);
         tags.add(item);
      }

      tag.put(STORAGE_FILE_NAME, tags);
      return tag;
   }

   // Load existing instance of saved data
   private static ServerBuildingsStore load(CompoundTag parentListTag, HolderLookup.Provider lookupProvider) {
      ServerBuildingsStore store = new ServerBuildingsStore();

      ListTag list = parentListTag.getList(STORAGE_FILE_NAME, Tag.TAG_COMPOUND);
      for (Tag t : list) {
         if (!(t instanceof CompoundTag itemTag))
            continue;

         Building.BuildingBuilder builder =
               Building.builder()
                     .buildingId(itemTag.getUUID(Building.FIELD_BUILDING_ID))
                     .settlementId(itemTag.getUUID(Building.FIELD_SETTLEMENT_ID))
                     .placerId(itemTag.getUUID(Building.FIELD_PLACER_ID))
                     .buildingType(BuildingType.valueOf(itemTag.getString(Building.FIELD_BUILDING_TYPE)))
                     .bounds(
                           new BuildingBounds(
                                 BlockPos.of(itemTag.getLong(Building.FIELD_CENTER_POS)),
                                 BlockPos.of(itemTag.getLong(Building.FIELD_LOWER_CORNER_POS)),
                                 BlockPos.of(itemTag.getLong(Building.FIELD_UPPER_CORNER_POS))));

         ListTag occupantIdsTag = itemTag.getList(Building.FIELD_LIST_OCCUPANTS, Tag.TAG_COMPOUND);
         List<UUID> occupantIds = Lists.newArrayList();
         for (Tag o : occupantIdsTag) {
            if (!(o instanceof CompoundTag co))
               continue;

            occupantIds.add(co.getUUID(Building.FIELD_LIST_ITEM_OCCUPANT_ID));
         }

         builder.occupantIds(occupantIds);
         Building building = builder.build();
         store.buildings.put(building.getBuildingId(), building);
      }

      return store;
   }

   public void replicateChange(Building building, StoreOperation operation) {
      assert buildings.containsKey(building.getBuildingId());
      PacketDistributor.sendToAllPlayers(building.toPacket(operation));
      NeoForge.EVENT_BUS.post(new BuildingUpdatedEvent(building, level, false));
      if (operation == StoreOperation.DELETE)
         NeoForge.EVENT_BUS.post(new BuildingDeletedEvent(building, level, false));
   }

   public void replicateFullToNewClient(ServerPlayer player) {
      for (Building building : buildings.values()) {
         PacketDistributor.sendToPlayer(player, building.toPacket(StoreOperation.INIT_NEW_CLIENT));
      }
   }

   public static void receiveSyncFromClient(Building.Packet packet, IPayloadContext context) {

      Building fromPacket = packet.building();

      UUID key = fromPacket.getBuildingId();
      Optional<Building> existing = INSTANCE.find(key);

      if (packet.storeOperation() == StoreOperation.ADD_OR_OVERWRITE
            || packet.storeOperation() == StoreOperation.INIT_NEW_CLIENT) {

         if (existing.isEmpty())
            INSTANCE.buildings.put(fromPacket.getBuildingId(), fromPacket);
         else
            existing.get().copyFrom(fromPacket);

         INSTANCE.replicateChange(fromPacket, packet.storeOperation());
         INSTANCE.setDirty();
         return;
      }

      if ((packet.storeOperation() == StoreOperation.UPDATE || packet.storeOperation() == StoreOperation.DELETE)
            && existing.isEmpty()) {
         LOGGER.error(
               "Client {} tried to sync {} storeOperation for building {} that did not exist on the server. This sync will be ignored.",
               packet.storeOperation(),
               context.player().getScoreboardName(),
               packet.building().toStringLite());
         return;
      }

      if (packet.storeOperation() == StoreOperation.DELETE) {
         Building removed = INSTANCE.buildings.remove(existing.get().getBuildingId());
         if (removed != null) {
            INSTANCE.replicateChange(removed, StoreOperation.DELETE);
            INSTANCE.setDirty();
         }
      } else if (packet.storeOperation() == StoreOperation.UPDATE) {
         existing.get().copyFrom(packet.building());
         INSTANCE.replicateChange(existing.get(), StoreOperation.UPDATE);
         INSTANCE.setDirty();
      }
   }
}
