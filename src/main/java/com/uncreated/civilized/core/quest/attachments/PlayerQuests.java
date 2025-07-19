package com.uncreated.civilized.core.quest.attachments;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
   public static final String CIVILIZED_QUEST_LIFECYCLE_STATUS = "civilized_quest_lifecycle_status";
   public static final String FIELD_QUEST_COMPLETED = "civilized_quest_completed";
   private final Map<String, ActivatedQuest> activatedQuests;

   public PlayerQuests() {
      activatedQuests = new HashMap<>();
   }

   private static String getQuestIdentifier(QuestType questType, @Nullable UUID vendorId) {

      if (questType.isVendorSpecific() && vendorId == null)
         throw new IllegalQuestIdentificationException(questType);

      if (!questType.isVendorSpecific() && vendorId != null)
         throw new IllegalQuestIdentificationException(questType, vendorId);

      return questType.getName() + "_" + vendorId;
   }

   private @Nullable ActivatedQuest getQuestByIdentifier(QuestType questType, @Nullable UUID vendorId) {
      return activatedQuests.get(getQuestIdentifier(questType, vendorId));
   }

   public ActivatedQuest getQuest(QuestType questType, @Nullable UUID vendorId) {
      return tryGetQuest(questType, vendorId).orElseThrow();
   }

   public Optional<ActivatedQuest> tryGetQuest(QuestType questType, @Nullable UUID vendorId) {
      return Optional.ofNullable(getQuestByIdentifier(questType, vendorId));
   }

   public Optional<ActivatedQuest> tryGetQuest(
         QuestType questType,
         @Nullable UUID vendorId,
         Function<ActivatedQuest, Boolean> filter) {
      Optional<ActivatedQuest> activatedQuest = tryGetQuest(questType, vendorId);
      if (activatedQuest.isPresent() && filter.apply(activatedQuest.get()))
         return activatedQuest;

      return Optional.empty();
   }

   public boolean checkQuestStatus(
         QuestType questType,
         @Nullable UUID vendorId,
         Function<ActivatedQuest, Boolean> check) {
      return checkQuestStatus(questType, vendorId, check, () -> false);
   }

   public boolean checkQuestStatus(
         QuestType questType,
         @Nullable UUID vendorId,
         Function<ActivatedQuest, Boolean> check,
         Supplier<Boolean> ifNoEntryFound) {
      Optional<ActivatedQuest> activatedQuest = tryGetQuest(questType, vendorId);
      if (activatedQuest.isPresent())
         return check.apply(activatedQuest.get());

      return ifNoEntryFound.get();
   }

   public void tryStartQuest(QuestType questType, @Nullable UUID vendorId) {
      Optional<ActivatedQuest> existingQuest =
            tryGetQuest(questType, vendorId, q -> q.getQuestType().isRepeatable() && q.isEnded());
      if (existingQuest.isEmpty()) {
         String id = getQuestIdentifier(questType, vendorId);
         ActivatedQuest activatedQuest = new ActivatedQuest(id, questType, vendorId);
         activatedQuests.put(id, activatedQuest);
         PacketDistributor.sendToServer(activatedQuest);
      }
   }

   public void updateQuest(QuestType questType, @Nullable UUID vendorId, Consumer<ActivatedQuest> updateFunction) {

      Optional<ActivatedQuest> activatedQuest = tryGetQuest(questType, vendorId);
      if (activatedQuest.isEmpty())
         return;

      updateFunction.accept(activatedQuest.get());
      PacketDistributor.sendToServer(activatedQuest.get());
   }

   public void completeQuest(QuestType questType, @Nullable UUID vendorId) {
      updateQuest(questType, vendorId, q -> q.setCompleted(true));
   }

   public void endQuest(QuestType questType, @Nullable UUID vendorId) {
      updateQuest(questType, vendorId, q -> {
         q.setLifecycleState(ActivatedQuest.QuestLifecycleState.ENDED);
         if (questType.isCheckpoint())
            q.setCompleted(true);
      });
   }

   public void completeCheckpointQuest(QuestType questType, @Nullable UUID vendorId) {
      String id = getQuestIdentifier(questType, vendorId);
      ActivatedQuest activatedQuest = new ActivatedQuest(id, questType, vendorId);
      activatedQuest.setLifecycleState(ActivatedQuest.QuestLifecycleState.ENDED);
      activatedQuest.setCompleted(true);
      activatedQuests.put(id, activatedQuest);
      PacketDistributor.sendToServer(activatedQuest);
   }

   public boolean hasActiveQuest(QuestType questType, @Nullable UUID vendorId) {
      return checkQuestStatus(questType, vendorId, ActivatedQuest::isStarted);
   }

   public boolean hasActiveIncompleteQuest(QuestType questType, @Nullable UUID vendorId) {
      return checkQuestStatus(questType, vendorId, q -> q.isStarted() && !q.isCompleted());
   }

   public boolean hasActiveCompletedQuest(QuestType questType, @Nullable UUID vendorId) {
      return checkQuestStatus(questType, vendorId, q -> q.isStarted() && q.isCompleted());
   }

   public boolean hasFullyCompleted(QuestType questType, @Nullable UUID vendorId) {
      if (questType.isRepeatable())
         return tryGetQuest(questType, vendorId).isEmpty();

      return checkQuestStatus(questType, vendorId, ActivatedQuest::isFullyCompleted);
   }

   public boolean hasNeverCompletedQuest(QuestType questType, @Nullable UUID vendorId) {
      Optional<ActivatedQuest> existingQuest = tryGetQuest(questType, vendorId);
      if (existingQuest.isPresent() && existingQuest.get().isCompleted() && existingQuest.get().isEnded())
         return questType.isRepeatable();

      return true;
   }

   public boolean hasFullyCompletedAButNeverBeenGivenB(QuestType A, @Nullable UUID vA, QuestType B, @Nullable UUID vB) {
      return hasFullyCompleted(A, vA) && hasNeverCompletedQuest(B, vB);
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
         element.putString(CIVILIZED_QUEST_LIFECYCLE_STATUS, quest.getLifecycleState().name());
         element.putBoolean(FIELD_QUEST_COMPLETED, quest.isCompleted());
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
                     ActivatedQuest.QuestLifecycleState.valueOf(elementTag.getString(CIVILIZED_QUEST_LIFECYCLE_STATUS)),
                     elementTag.getBoolean(FIELD_QUEST_COMPLETED));

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

   public static class IllegalQuestIdentificationException extends RuntimeException {
      public IllegalQuestIdentificationException(QuestType questType, @NotNull UUID vendorId) {
         super(
               String.format(
                     "Quest '%s' is not vendor-specific, yet a vendorId (%s) was provided.",
                     questType,
                     vendorId));
      }

      public IllegalQuestIdentificationException(QuestType questType) {
         super(String.format("Quest '%s' is vendor-specific, yet no vendorId was provided.", questType));
      }
   }
}
