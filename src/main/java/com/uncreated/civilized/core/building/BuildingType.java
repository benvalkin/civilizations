package com.uncreated.civilized.core.building;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import com.uncreated.civilized.core.building.entity.LoadedBuilding;
import com.uncreated.civilized.core.building.entity.behaviour.BuildingBehaviour;
import com.uncreated.civilized.core.building.production.bills.ProductionType;
import com.uncreated.civilized.core.building.state.BuildingState;
import com.uncreated.civilized.core.villagerinfo.VillagerOccupation;
import com.uncreated.civilized.ui.style.Colors;

import lombok.Builder;
import lombok.experimental.Accessors;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

@Accessors(fluent = true)
@Builder(builderMethodName = "internalBuilder")
public abstract class BuildingType {
   private final String value;
   private final boolean isPermanentResidence;
   private final boolean isTemporaryResidence;
   private final boolean isWorksite;
   private final boolean isAnimalFarm;
   @Builder.Default
   private final Function<Building, BuildingState> createState = BuildingState::new;
   @Builder.Default
   private final Function<LoadedBuilding, BuildingBehaviour> createBehaviour = BuildingBehaviour::new;
   @Builder.Default
   private final List<ProductionType> supportedProductionTypes = List.of();
   @Builder.Default
   private final VillagerOccupation occupation = VillagerOccupation.UNEMPLOYED;

   public static BuildingTypeBuilder builder(String value) {
      return internalBuilder().value(value);
   }

   public boolean isResidence() {
      return isPermanentResidence || isTemporaryResidence;
   }

   public boolean isArtisanHouse() {
      return !supportedProductionTypes.isEmpty();
   }

   public String translationKey() {
      return "building." + value;
   }

   public MutableComponent translation() {
      return Component.translatableWithFallback(translationKey(), value.replace("_", " "))
            .withColor(Colors.BUILDING_LIGHT);
   }

   public MutableComponent translationDark() {
      return Component.translatableWithFallback(translationKey(), value.replace("_", " "))
            .withColor(Colors.BUILDING_DARK);
   }

   @Override
   public boolean equals(Object o) {
      if (o == null || getClass() != o.getClass())
         return false;
      BuildingType that = (BuildingType) o;
      return Objects.equals(value, that.value);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(value);
   }

   @Override
   public String toString() {
      return value;
   }
}
