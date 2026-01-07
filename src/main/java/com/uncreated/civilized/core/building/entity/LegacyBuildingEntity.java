package com.uncreated.civilized.core.building.entity;

import static com.uncreated.civilized.core.building.Building.FIELD_BUILDING_ID;

import java.util.UUID;

import com.uncreated.civilized.core.building.Building;
import com.uncreated.civilized.core.building.ClientBuildingStore;
import com.uncreated.civilized.core.building.ServerBuildingsStore;
import com.uncreated.civilized.core.building.crafting.CraftingMachine;

import lombok.Getter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.entity.IEntityWithComplexSpawn;

@Deprecated
public class LegacyBuildingEntity extends Entity implements IEntityWithComplexSpawn {

   private UUID buildingId;

   @Getter
   private Building building;

   @Getter
   private CraftingMachine craftingMachine; // only available on server

   // server constructor
   public LegacyBuildingEntity(EntityType<?> entityType, Level level) {
      super(entityType, level);
      this.craftingMachine = new CraftingMachine();
   }

   public LegacyBuildingEntity(EntityType<?> entityType, Level level, Building building) {
      super(entityType, level);
      this.building = building;
      this.buildingId = building.getBuildingId();
      this.setPos(building.getBlockPos().getCenter());
   }

   public void initBrandNew() {

   }

   public void initFromSave() {
      building = ServerBuildingsStore.INSTANCE.get(buildingId);
   }

   public void serverFinalizeSpawn() {

   }

   @Override
   public void writeSpawnData(RegistryFriendlyByteBuf buf) {
      buf.writeUUID(buildingId);
   }

   @Override
   public void readSpawnData(RegistryFriendlyByteBuf buf) {
      building = ClientBuildingStore.INSTANCE.get(buf.readUUID());
   }

   @Override
   protected void readAdditionalSaveData(CompoundTag compoundTag) {
      // when spawning a new entity, readAdditionalSaveData will still run, but the NBT data won't have our fields
      // yet, so we need to check
      if (compoundTag.hasUUID(FIELD_BUILDING_ID))
         buildingId = compoundTag.getUUID(FIELD_BUILDING_ID);
   }

   @Override
   protected void addAdditionalSaveData(CompoundTag compoundTag) {
      compoundTag.putUUID(FIELD_BUILDING_ID, buildingId);
   }

   @Override
   protected void defineSynchedData(SynchedEntityData.Builder builder) {

   }

   @Override
   public boolean hurtServer(ServerLevel serverLevel, DamageSource damageSource, float v) {
      return false;
   }

   @Override
   public boolean shouldRender(double x, double y, double z) {
      return false;
   }
}
