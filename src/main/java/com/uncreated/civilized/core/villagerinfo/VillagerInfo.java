package com.uncreated.civilized.core.villagerinfo;

import static com.uncreated.civilized.CivilizedMod.CIVILIZED_MOD_ID;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import javax.annotation.Nullable;

import com.mojang.datafixers.util.Pair;
import com.uncreated.civilized.core.StoreOperation;
import com.uncreated.civilized.core.building.Building;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

@Getter
@Builder
public class VillagerInfo {

   public static StreamCodec<FriendlyByteBuf, VillagerInfo> CODEC =
         StreamCodec.ofMember(VillagerInfo::encode, VillagerInfo::decode);

   // The stream decoder reference
   public static VillagerInfo decode(FriendlyByteBuf buffer) {
      VillagerInfoBuilder builder =
            VillagerInfo.builder()
                  .villagerId(buffer.readUUID())
                  .isDeceased(buffer.readBoolean())
                  .firstName(buffer.readUtf())
                  .lastName(buffer.readUtf())
                  .occupation(buffer.readEnum(VillagerOccupation.class))
                  .settlementId(buffer.readNullable((b -> b.readUUID())))
                  .homeBuildingId(buffer.readNullable((b -> b.readUUID())))
                  .npcRoles(buffer.readCollection(ArrayList::new, b -> b.readEnum(VillagerNpcRole.class)))
                  .gender(buffer.readEnum(Gender.class));

      return builder.build();
   }

   // The stream encoder reference
   public void encode(FriendlyByteBuf buffer) {
      buffer.writeUUID(villagerId);
      buffer.writeBoolean(isDeceased);
      buffer.writeUtf(firstName);
      buffer.writeUtf(lastName);
      buffer.writeEnum(occupation);
      buffer.writeNullable(settlementId, (b, v) -> b.writeUUID(v));
      buffer.writeNullable(homeBuildingId, (b, v) -> b.writeUUID(v));
      buffer.writeCollection(npcRoles, FriendlyByteBuf::writeEnum);
      buffer.writeEnum(gender);
   }

   public static final String FIELD_VILLAGER_ID = "villager_id";
   public static final String FIELD_IS_DECEASED = "is_deceased";
   public static final String FIELD_SETTLEMENT_ID = "settlement_id";
   public static final String FIELD_HOME_BUILDING_ID = "home_building_id";
   public static final String FIELD_FIRST_NAME = "first_name";
   public static final String FIELD_LAST_NAME = "last_name";
   public static final String FIELD_VILLAGER_OCCUPATION = "field_villager_occupation";
   public static final String FIELD_VILLAGER_NPC_ROLES = "field_villager_npc_roles";
   public static final String FIELD_VILLAGER_NPC_ROLE = "field_villager_npc_role";
   public static final String FIELD_VILLAGER_GENDER = "field_villager_gender";

   private UUID villagerId;
   @Setter
   private boolean isDeceased;
   @Setter
   @Builder.Default
   private String firstName = "";
   @Setter
   @Builder.Default
   private String lastName = "";
   @Setter
   @Builder.Default
   private VillagerOccupation occupation = VillagerOccupation.UNEMPLOYED;
   @Builder.Default
   private List<VillagerNpcRole> npcRoles = new ArrayList<>();
   @Setter
   private @Nullable UUID settlementId;
   @Setter
   private @Nullable UUID homeBuildingId;
   @Setter
   private @Nullable UUID primaryWorksiteId;
   private Gender gender;

   public boolean hasName() {
      return !firstName.isEmpty() && !lastName.isEmpty();
   }

   public String getFullName() {

      if (!hasName()) {
         return "Unnamed Villager";
      }

      return firstName + " " + lastName;
   }

   public Component getFullNameComponent() {
      if (!hasName())
         return Component.empty();

      return Component.literal(getFullName());
   }

   public static Pair<String, String> generateRandomName() {
      return switch (new Random().nextInt(6)) {
      case 0 -> Pair.of("Ryaan", "van Reynoldus");
      case 1 -> Pair.of("Koos", "Evans");
      case 2 -> Pair.of("Brad", "Pietermaritzberg");
      case 3 -> Pair.of("Daaniel", "Rooikloof");
      case 4 -> Pair.of("Keanu", "van Riebeeck");
      default -> Pair.of("Cornelius", "Hemsworth");
      };
   }

   public Packet toPacket() {
      return new Packet(this, StoreOperation.UPDATE);
   }

   public Packet toPacket(StoreOperation operation) {
      return new Packet(this, operation);
   }

   public void copyFrom(VillagerInfo other) {
      isDeceased = other.isDeceased;
      firstName = other.firstName;
      lastName = other.lastName;
      occupation = other.occupation;
      settlementId = other.settlementId;
      homeBuildingId = other.homeBuildingId;
      npcRoles = other.npcRoles;
      gender = other.gender;
   }

   public String toStringLite() {
      return String.format(
            "{name: %s %s - occupation: %s, villagerId: %s settlementId: %s}",
            firstName,
            lastName,
            villagerId,
            settlementId,
            occupation);
   }

   public boolean isOccupantOf(Building building) {
      return building.getBuildingId().equals(homeBuildingId);
   }

   public record Packet(VillagerInfo villager, StoreOperation storeOperation) implements CustomPacketPayload {

      public static final Type<Packet> SYNC_TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(CIVILIZED_MOD_ID, "sync_villagerinfo"));

      public static StreamCodec<FriendlyByteBuf, Packet> STREAM_CODEC =
            StreamCodec.ofMember(Packet::encode, Packet::decode);

      public static Packet decode(FriendlyByteBuf buffer) {
         return new Packet(VillagerInfo.decode(buffer), buffer.readEnum(StoreOperation.class));
      }

      public void encode(FriendlyByteBuf buffer) {
         villager.encode(buffer);
         buffer.writeEnum(storeOperation);
      }

      @Override
      public Type<? extends CustomPacketPayload> type() {
         return SYNC_TYPE;
      }
   }
}
