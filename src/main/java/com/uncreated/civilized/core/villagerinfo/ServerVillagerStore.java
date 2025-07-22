package com.uncreated.civilized.core.villagerinfo;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import com.uncreated.civilized.core.StoreOperation;
import com.uncreated.civilized.core.villagerinfo.events.model.VillagerInfoUpdatedEvent;
import com.uncreated.civilized.entity.CivilizedVillager;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.saveddata.SavedData;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class ServerVillagerStore extends VillagerStore {

   public static ServerVillagerStore INSTANCE;

   public static void loadServer(MinecraftServer server) {
      INSTANCE =
            server.overworld()
                  .getDataStorage()
                  .computeIfAbsent(
                        new SavedData.Factory<>(ServerVillagerStore::createDefault, ServerVillagerStore::load),
                        STORAGE_FILE_NAME);
   }

   public static final String STORAGE_FILE_NAME = "civilized_villagers";

   private static ServerVillagerStore createDefault() {
      return new ServerVillagerStore();
   }

   @Override
   public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {

      ListTag tags = new ListTag();
      for (VillagerInfo villagerInfo : villagers.values()) {
         CompoundTag item = new CompoundTag();
         item.putUUID(VillagerInfo.FIELD_VILLAGER_ID, villagerInfo.getVillagerId());
         item.putBoolean(VillagerInfo.FIELD_IS_DECEASED, villagerInfo.isDeceased());
         if (villagerInfo.getSettlementId() != null)
            item.putUUID(VillagerInfo.FIELD_SETTLEMENT_ID, villagerInfo.getSettlementId());
         if (villagerInfo.getHomeBuildingId() != null)
            item.putUUID(VillagerInfo.FIELD_HOME_BUILDING_ID, villagerInfo.getHomeBuildingId());
         item.putString(VillagerInfo.FIELD_FIRST_NAME, villagerInfo.getFirstName());
         item.putString(VillagerInfo.FIELD_LAST_NAME, villagerInfo.getLastName());
         item.putString(VillagerInfo.FIELD_VILLAGER_OCCUPATION, villagerInfo.getOccupation().name());

         ListTag npcRoles = new ListTag();
         npcRoles.addAll(villagerInfo.getNpcRoles().stream().map(role -> {
            CompoundTag t = new CompoundTag();
            t.putString(VillagerInfo.FIELD_VILLAGER_NPC_ROLE, role.name());
            return t;
         }).toList());
         item.put(VillagerInfo.FIELD_VILLAGER_NPC_ROLES, npcRoles);

         tags.add(item);
      }

      tag.put(STORAGE_FILE_NAME, tags);
      return tag;
   }

   // Load existing instance of saved data
   private static ServerVillagerStore load(CompoundTag tag, HolderLookup.Provider lookupProvider) {
      ServerVillagerStore store = new ServerVillagerStore();

      ListTag list = tag.getList(STORAGE_FILE_NAME, Tag.TAG_COMPOUND);
      for (Tag i : list) {
         if (!(i instanceof CompoundTag itemTag)) {
            continue;
         }
         VillagerInfo.VillagerInfoBuilder builder =
               VillagerInfo.builder()
                     .villagerId(itemTag.getUUID(VillagerInfo.FIELD_VILLAGER_ID))
                     .isDeceased(itemTag.getBoolean(VillagerInfo.FIELD_IS_DECEASED))
                     .firstName(itemTag.getString(VillagerInfo.FIELD_FIRST_NAME))
                     .lastName(itemTag.getString(VillagerInfo.FIELD_LAST_NAME))
                     .occupation(VillagerOccupation.valueOf(itemTag.getString(VillagerInfo.FIELD_VILLAGER_OCCUPATION)));

         if (itemTag.hasUUID(VillagerInfo.FIELD_SETTLEMENT_ID))
            builder.settlementId(itemTag.getUUID(VillagerInfo.FIELD_SETTLEMENT_ID));
         if (itemTag.hasUUID(VillagerInfo.FIELD_HOME_BUILDING_ID))
            builder.homeBuildingId(itemTag.getUUID(VillagerInfo.FIELD_HOME_BUILDING_ID));

         ListTag npcRoles = itemTag.getList(VillagerInfo.FIELD_VILLAGER_NPC_ROLES, Tag.TAG_COMPOUND);
         builder.npcRoles(
               npcRoles.stream()
                     .map(
                           e -> VillagerNpcRole
                                 .valueOf(((CompoundTag) e).getString(VillagerInfo.FIELD_VILLAGER_NPC_ROLE)))
                     .collect(Collectors.toList()));

         VillagerInfo villagerInfo = builder.build();
         store.villagers.put(villagerInfo.getVillagerId(), villagerInfo);
      }

      return store;
   }

   public void replicateChange(VillagerInfo villagerInfo, StoreOperation operation) {
      assert villagers.containsKey(villagerInfo.getVillagerId());
      PacketDistributor.sendToAllPlayers(villagerInfo.toPacket(operation));
      NeoForge.EVENT_BUS.post(new VillagerInfoUpdatedEvent(villagerInfo, false));
   }

   public void replicateFullToNewClient(ServerPlayer player) {
      for (VillagerInfo villagerInfo : villagers.values()) {
         PacketDistributor.sendToPlayer(player, villagerInfo.toPacket(StoreOperation.INIT_NEW_CLIENT));
      }
   }

   public static void receiveSyncFromClient(VillagerInfo.Packet packet, IPayloadContext context) {

      VillagerInfo fromPacket = packet.villager();

      UUID key = fromPacket.getVillagerId();
      Optional<VillagerInfo> existing = INSTANCE.find(key);

      if (packet.storeOperation() == StoreOperation.ADD_OR_OVERWRITE
            || packet.storeOperation() == StoreOperation.INIT_NEW_CLIENT) {

         if (existing.isEmpty())
            INSTANCE.villagers.put(fromPacket.getVillagerId(), fromPacket);
         else
            existing.get().copyFrom(fromPacket);

         INSTANCE.setDirty();
         INSTANCE.replicateChange(fromPacket, packet.storeOperation());
         return;
      }

      if ((packet.storeOperation() == StoreOperation.UPDATE || packet.storeOperation() == StoreOperation.DELETE)
            && existing.isEmpty()) {
         LOGGER.error(
               "Client {} tried to sync {} store operation for villager {} that did not exist on the server. This sync will be ignored.",
               packet.storeOperation(),
               context.player().getScoreboardName(),
               packet.villager().toStringLite());
         return;
      }

      if (packet.storeOperation() == StoreOperation.DELETE) {
         VillagerInfo removed = INSTANCE.villagers.remove(existing.get().getVillagerId());
         if (removed != null) {
            INSTANCE.replicateChange(removed, StoreOperation.DELETE);
            INSTANCE.setDirty();
         }
      } else if (packet.storeOperation() == StoreOperation.UPDATE) {
         existing.get().copyFrom(packet.villager());
         INSTANCE.replicateChange(existing.get(), StoreOperation.UPDATE);
         INSTANCE.setDirty();
      }
   }

   public VillagerInfo getOrAdd(CivilizedVillager villager) {
      Objects.requireNonNull(villager.getVillagerId());
      VillagerInfo villagerInfo = villagers.get(villager.getVillagerId());
      if (villagerInfo != null) {
         return villagerInfo;
      }

      VillagerInfo newInfo =
            VillagerInfo.builder().entityId(villager.getId()).villagerId(villager.getVillagerId()).build();

      villagers.put(villager.getVillagerId(), newInfo);
      return newInfo;
   }

   public Optional<VillagerInfo> delete(CivilizedVillager villager) {
      return Optional.ofNullable(villagers.remove(villager.getVillagerId()));
   }
}
