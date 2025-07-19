package com.uncreated.civilized.core.quest;

import static com.uncreated.civilized.CivilizedMod.CIVILIZED_MOD_ID;

import java.util.UUID;

import javax.annotation.Nullable;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

@Getter
public class ActivatedQuest implements CustomPacketPayload {
   private final String id;
   private final QuestType questType;
   @Nullable
   private final UUID vendorId;
   @Setter
   private int points;
   @Setter
   private QuestLifecycleState lifecycleState;
   @Setter
   private boolean completed;

   public ActivatedQuest(
         String id,
         QuestType questType,
         @Nullable UUID vendorId,
         int points,
         QuestLifecycleState lifecycleState,
         boolean completed) {
      this.id = id;
      this.questType = questType;
      this.vendorId = vendorId;
      this.points = points;
      this.lifecycleState = lifecycleState;
      this.completed = completed;
   }

   public ActivatedQuest(String id, QuestType questType, @Nullable UUID vendorId) {
      this.id = id;
      this.questType = questType;
      this.vendorId = vendorId;
      this.points = 0;
      this.lifecycleState = QuestLifecycleState.STARTED;
      this.completed = false;
   }

   public boolean isEnded() {
      return this.lifecycleState == QuestLifecycleState.ENDED;
   }

   public boolean isStarted() {
      return this.lifecycleState == QuestLifecycleState.STARTED;
   }

   public boolean isStartedButNotCompleted() {
      return this.lifecycleState == QuestLifecycleState.STARTED && !this.completed;
   }

   public boolean isStartedAndCompleted() {
      return this.lifecycleState == QuestLifecycleState.STARTED && !this.completed;
   }

   public boolean isFullyCompleted() {
      return this.lifecycleState == QuestLifecycleState.ENDED && this.isCompleted();
   }

   @Override
   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }

   public static final CustomPacketPayload.Type<ActivatedQuest> TYPE =
         new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(CIVILIZED_MOD_ID, "sync_player_quest"));

   public static final StreamCodec<FriendlyByteBuf, ActivatedQuest> STREAM_CODEC = StreamCodec.of((buffer, quest) -> {
      buffer.writeUtf(quest.getId());
      buffer.writeUtf(quest.getQuestType().getName());
      buffer.writeNullable(quest.getVendorId(), (b, uuid) -> b.writeUUID(uuid));
      buffer.writeInt(quest.getPoints());
      buffer.writeEnum(quest.getLifecycleState());
      buffer.writeBoolean(quest.isCompleted());
   },
         buffer -> new ActivatedQuest(
               buffer.readUtf(),
               Quests.get(buffer.readUtf()),
               buffer.readNullable(b -> b.readUUID()),
               buffer.readInt(),
               buffer.readEnum(QuestLifecycleState.class),
               buffer.readBoolean()));

   public enum QuestLifecycleState {
      STARTED, ENDED
   }
}
