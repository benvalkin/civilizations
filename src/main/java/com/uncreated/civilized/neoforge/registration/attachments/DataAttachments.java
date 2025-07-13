package com.uncreated.civilized.neoforge.registration.attachments;

import static com.uncreated.civilized.CivilizedMod.CIVILIZED_MOD_ID;

import java.util.UUID;
import java.util.function.Supplier;

import org.jetbrains.annotations.Nullable;

import lombok.Getter;
import lombok.Setter;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class DataAttachments {

   // Create the DeferredRegister for attachment types
   public static final DeferredRegister<AttachmentType<?>> ATTACHMENTS =
         DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, CIVILIZED_MOD_ID);

   // Serialization via codec
   public static final Supplier<AttachmentType<LinkedBuilding>> LINKED_BUILDING =
         ATTACHMENTS.register(
               "civilized_building_id",
               () -> AttachmentType.builder(() -> new LinkedBuilding())
                     // .serialize(new IAttachmentSerializer<>() {
                     // @Override
                     // public LinkedBuilding read(
                     // IAttachmentHolder attachmentHolder,
                     // Tag tag,
                     // HolderLookup.Provider provider) {
                     //
                     // LinkedBuilding result = new LinkedBuilding();
                     // if (tag instanceof CompoundTag ct && ct.hasUUID("civilized_building_id"))
                     // result.setBuildingId(ct.getUUID("civilized_building_id"));
                     // return result;
                     // }
                     //
                     // @Override
                     // public @Nullable Tag write(LinkedBuilding linkedBuilding, HolderLookup.Provider provider) {
                     // CompoundTag result = new CompoundTag();
                     // if (linkedBuilding.getBuildingId() != null)
                     // result.putUUID("civilized_building_id", linkedBuilding.getBuildingId());
                     //
                     // return result;
                     // }
                     // })
                     .build());

   @Getter
   @Setter
   public static class LinkedBuilding {

      public static final String FIELD_BUILDING_IS_BUILDING = "civilized_building_id";

      public LinkedBuilding() {
      }

      public LinkedBuilding(@Nullable UUID buildingId) {
         this.buildingId = buildingId;
      }

      private @Nullable UUID buildingId;
   }
}
