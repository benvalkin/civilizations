package com.uncreated.civilized.core.quest;

import static net.neoforged.neoforge.internal.versions.neoforge.NeoForgeVersion.MOD_ID;

import java.util.UUID;

import javax.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

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
   private QuestStatus status;

   public ActivatedQuest(String id, QuestType questType, @Nullable UUID vendorId, int points, QuestStatus status) {
      this.id = id;
      this.questType = questType;
      this.vendorId = vendorId;
      this.points = points;
      this.status = status;
   }

   public ActivatedQuest(String id, QuestType questType, @NotNull UUID vendorId) {
      this.id = id;
      this.questType = questType;
      this.vendorId = vendorId;
      this.points = 0;
      this.status = QuestStatus.STARTED;
   }

   public ActivatedQuest(String id, QuestType questType) {
      this.id = questType.getName();
      this.questType = questType;
      this.vendorId = null;
      this.points = 0;
      this.status = QuestStatus.STARTED;
   }

   public boolean isComplete() {
      return this.status == QuestStatus.COMPLETE;
   }

   public boolean isEnded() {
      return this.status == QuestStatus.ENDED;
   }

   public boolean isActive() {
      return !isEnded();
   }

   @Override
   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }

   public static final CustomPacketPayload.Type<ActivatedQuest> TYPE =
         new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(MOD_ID, "sync_player_quest"));

   public static final StreamCodec<FriendlyByteBuf, ActivatedQuest> STREAM_CODEC = StreamCodec.of((buffer, quest) -> {
      buffer.writeUtf(quest.getId());
      buffer.writeUtf(quest.getQuestType().getName());
      buffer.writeNullable(quest.getVendorId(), (b, uuid) -> b.writeUUID(uuid));
      buffer.writeInt(quest.getPoints());
      buffer.writeEnum(quest.getStatus());
   },
         buffer -> new ActivatedQuest(
               buffer.readUtf(),
               Quests.get(buffer.readUtf()),
               buffer.readNullable(b -> b.readUUID()),
               buffer.readInt(),
               buffer.readEnum(QuestStatus.class)));

   public enum QuestStatus {
      STARTED, COMPLETE, ENDED
   }
}
