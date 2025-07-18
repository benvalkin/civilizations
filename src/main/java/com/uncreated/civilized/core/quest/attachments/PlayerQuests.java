package com.uncreated.civilized.core.quest.attachments;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

import org.jetbrains.annotations.NotNull;

import com.uncreated.civilized.core.quest.ActivatedQuest;
import com.uncreated.civilized.core.quest.QuestType;
import com.uncreated.civilized.core.quest.Quests;
import com.uncreated.civilized.neoforge.registration.attachments.DataAttachments;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class PlayerQuests implements INBTSerializable<ListTag>, Iterable<ActivatedQuest> {
   public static final String FIELD_QUEST_ID = "civilized_quest_id";
   public static final String FIELD_QUEST_TYPE_ID = "civilized_quest_type";
   public static final String FIELD_QUEST_VENDOR_ID = "civilized_quest_vendor_id";
   public static final String FIELD_QUEST_POINTS = "civilized_quest_points";
   public static final String FIELD_QUEST_STATUS = "civilized_quest_status";
   private final Map<String, ActivatedQuest> activatedQuests;

   public PlayerQuests() {
      activatedQuests = new HashMap<>();
   }

   private static String getCompoundIdentifier(QuestType questType, @NotNull UUID vendorId) {
      return questType.getName() + "_" + vendorId;
   }

   public boolean hasStartedOrCompleted(QuestType questType) {
      return activatedQuests.containsKey(questType.getName());
   }

   public boolean hasStartedOrCompleted(QuestType questType, @NotNull UUID vendorId) {
      return activatedQuests.containsKey(getCompoundIdentifier(questType, vendorId));
   }

   public boolean hasActiveQuest(QuestType questType) {
      ActivatedQuest quest = activatedQuests.get(questType.getName());
      return quest != null && !quest.isComplete();
   }

   public boolean hasActiveQuest(QuestType questType, @NotNull UUID vendorId) {
      ActivatedQuest quest = activatedQuests.get(getCompoundIdentifier(questType, vendorId));
      return quest != null && !quest.isComplete();
   }

   public boolean hasCompleted(QuestType questType) {
      ActivatedQuest quest = activatedQuests.get(questType.getName());
      return quest != null && quest.isComplete();
   }

   public boolean hasCompleted(QuestType questType, @NotNull UUID vendorId) {
      ActivatedQuest quest = activatedQuests.get(getCompoundIdentifier(questType, vendorId));
      return quest != null && quest.isComplete();
   }

   public Optional<ActivatedQuest> tryGetQuest(QuestType questType, @NotNull UUID vendorId) {
      return Optional.ofNullable(activatedQuests.get(getCompoundIdentifier(questType, vendorId)));
   }

   public Optional<ActivatedQuest> tryGetQuest(QuestType questType) {
      return Optional.ofNullable(activatedQuests.get(questType.getName()));
   }

   public Optional<ActivatedQuest> tryGetQuest(QuestType questType, ActivatedQuest.QuestStatus requireStatus) {
      Optional<ActivatedQuest> activatedQuest = tryGetQuest(questType);
      if (activatedQuest.isPresent() && activatedQuest.get().getStatus() == requireStatus)
         return activatedQuest;

      return Optional.empty();
   }

   public Optional<ActivatedQuest> tryGetQuest(
         QuestType questType,
         @NotNull UUID vendorId,
         ActivatedQuest.QuestStatus requireStatus) {
      Optional<ActivatedQuest> activatedQuest = tryGetQuest(questType, vendorId);
      if (activatedQuest.isPresent() && activatedQuest.get().getStatus() == requireStatus)
         return activatedQuest;

      return Optional.empty();
   }

   public ActivatedQuest getQuest(QuestType questType) {
      return tryGetQuest(questType).orElseThrow();
   }

   public ActivatedQuest getQuest(QuestType questType, @NotNull UUID vendorId) {
      return tryGetQuest(questType, vendorId).orElseThrow();
   }

   public void tryStartQuest(QuestType questType) {
      Optional<ActivatedQuest> existingQuest = tryGetQuest(questType);
      if (existingQuest.isEmpty()
            || (existingQuest.get().getQuestType().isRepeatable() && existingQuest.get().isEnded())) {
         String id = questType.getName();
         ActivatedQuest activatedQuest = new ActivatedQuest(id, questType);
         activatedQuests.put(id, activatedQuest);
         PacketDistributor.sendToServer(activatedQuest);
      }
   }

   public void tryStartQuest(QuestType questType, @NotNull UUID vendorId) {
      Optional<ActivatedQuest> existingQuest = tryGetQuest(questType, vendorId);
      if (existingQuest.isEmpty()
            || (existingQuest.get().getQuestType().isRepeatable() && existingQuest.get().isEnded())) {
         String id = getCompoundIdentifier(questType, vendorId);
         ActivatedQuest activatedQuest = new ActivatedQuest(id, questType, vendorId);
         activatedQuests.put(id, activatedQuest);
         PacketDistributor.sendToServer(activatedQuest);
      }
   }

   public void updateQuestStatus(QuestType questType, ActivatedQuest.QuestStatus status) {

      Optional<ActivatedQuest> activatedQuest = tryGetQuest(questType);
      if (activatedQuest.isEmpty())
         return;

      activatedQuest.get().setStatus(status);
      PacketDistributor.sendToServer(activatedQuest.get());
   }

   public void updateQuestStatus(QuestType questType, @NotNull UUID vendorId, ActivatedQuest.QuestStatus status) {

      Optional<ActivatedQuest> activatedQuest = tryGetQuest(questType, vendorId);
      if (activatedQuest.isEmpty())
         return;

      activatedQuest.get().setStatus(status);
      PacketDistributor.sendToServer(activatedQuest.get());
   }

   public void completeQuest(QuestType questType) {
      updateQuestStatus(questType, ActivatedQuest.QuestStatus.COMPLETE);
   }

   public void completeQuest(QuestType questType, @NotNull UUID vendorId) {
      updateQuestStatus(questType, vendorId, ActivatedQuest.QuestStatus.COMPLETE);
   }

   public void endQuest(QuestType questType) {
      updateQuestStatus(questType, ActivatedQuest.QuestStatus.ENDED);
   }

   public void endQuest(QuestType questType, @NotNull UUID vendorId) {
      updateQuestStatus(questType, vendorId, ActivatedQuest.QuestStatus.ENDED);
   }

   @Override
   public ListTag serializeNBT(HolderLookup.Provider provider) {
      ListTag listTag = new ListTag();
      for (ActivatedQuest quest : activatedQuests.values()) {
         CompoundTag element = new CompoundTag();
         element.putString(FIELD_QUEST_ID, quest.getId());
         element.putString(FIELD_QUEST_TYPE_ID, quest.getQuestType().getName());
         if (quest.getVendorId() != null)
            element.putUUID(FIELD_QUEST_VENDOR_ID, quest.getVendorId());
         element.putInt(FIELD_QUEST_POINTS, quest.getPoints());
         element.putString(FIELD_QUEST_STATUS, quest.getStatus().name());
         listTag.add(element);
      }
      return listTag;
   }

   @Override
   public void deserializeNBT(HolderLookup.Provider provider, ListTag listTag) {

      activatedQuests.clear();
      for (Tag tag : listTag) {
         if (!(tag instanceof CompoundTag elementTag))
            continue;

         ActivatedQuest quest =
               new ActivatedQuest(
                     elementTag.getString(FIELD_QUEST_ID),
                     Quests.get(elementTag.getString(FIELD_QUEST_TYPE_ID)),
                     elementTag.hasUUID(FIELD_QUEST_VENDOR_ID) ? elementTag.getUUID(FIELD_QUEST_VENDOR_ID) : null,
                     elementTag.getInt(FIELD_QUEST_POINTS),
                     ActivatedQuest.QuestStatus.valueOf(elementTag.getString(FIELD_QUEST_STATUS)));

         activatedQuests.put(quest.getId(), quest);
      }
   }

   public static void receiveSyncFromServer(ActivatedQuest quest, IPayloadContext context) {
      PlayerQuests quests = context.player().getData(DataAttachments.QUESTS);
      if (quest.getQuestType().isRepeatable() && quest.isEnded()) {
         quests.activatedQuests.remove(quest.getId());
      } else
         quests.activatedQuests.put(quest.getId(), quest);
   }

   public static void receiveSyncFromClient(ActivatedQuest quest, IPayloadContext context) {
      PlayerQuests quests = context.player().getData(DataAttachments.QUESTS);
      if (quest.getQuestType().isRepeatable() && quest.isEnded()) {
         quests.activatedQuests.remove(quest.getId());
      } else
         quests.activatedQuests.put(quest.getId(), quest);

      PacketDistributor.sendToPlayer((ServerPlayer) context.player(), quest);
   }

   public void syncAllToClient(ServerPlayer player) {
      forEach(quest -> PacketDistributor.sendToPlayer(player, quest));
   }

   @Override
   public @NotNull Iterator<ActivatedQuest> iterator() {
      return activatedQuests.values().iterator();
   }

   @Override
   public void forEach(Consumer<? super ActivatedQuest> action) {
      activatedQuests.values().forEach(action);
   }
}
