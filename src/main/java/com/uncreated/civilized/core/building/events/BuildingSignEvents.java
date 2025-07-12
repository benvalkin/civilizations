package com.uncreated.civilized.core.building.events;

import java.util.List;

import com.uncreated.civilized.CivilizedMod;
import com.uncreated.civilized.core.building.Building;
import com.uncreated.civilized.core.building.BuildingType;
import com.uncreated.civilized.core.building.events.model.BuildingUpdatedEvent;
import com.uncreated.civilized.core.building.util.BuildingUtil;
import com.uncreated.civilized.core.settlement.ServerSettlementsStore;
import com.uncreated.civilized.core.settlement.Settlement;
import com.uncreated.civilized.core.villagerinfo.ServerVillagerStore;
import com.uncreated.civilized.core.villagerinfo.VillagerInfo;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.SignText;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber(modid = CivilizedMod.CIVILIZED_MOD_ID)
public class BuildingSignEvents {

   @SubscribeEvent
   public static void onBuildingUpdated(BuildingUpdatedEvent event) {

      if (event.getLevel() == null || event.isClientside())
         return;

      Building building = event.getBuilding();

      List<SignBlockEntity> signEntities =
            event.getBuilding()
                  .getBounds()
                  .getBlockEntitiesInsideBuilding(event.getLevel())
                  .stream()
                  .filter(b -> b instanceof SignBlockEntity)
                  .map(b -> (SignBlockEntity) b)
                  .toList();

      if (signEntities.isEmpty())
         return;

      SignBlockEntity primary = signEntities.getFirst();

      SignText updatedSignText = getBuildingSignText(building);
      primary.setText(updatedSignText, true);
   }

   public static SignText getBuildingSignText(Building building) {
      List<VillagerInfo> occupants = BuildingUtil.getOccupants(building, ServerVillagerStore.INSTANCE);
      Settlement settlement = ServerSettlementsStore.INSTANCE.get(building.getSettlementId());

      Component[] signTextComponents = getSignTextComponents(building, settlement, occupants);
      return new SignText(signTextComponents, signTextComponents, DyeColor.BLACK, false);

   }

   private static Component[] getSignTextComponents(
         Building building,
         Settlement settlement,
         List<VillagerInfo> occupants) {
      if (building.getBuildingType() == BuildingType.TRADING_POST) {
         return new Component[] { building.getBuildingType().translation(),
               settlement.displayNameTranslation().withStyle(ChatFormatting.ITALIC), Component.empty(),
               Component.empty() };
      } else if (building.getBuildingType().isPermanentResidence()) {
         return new Component[] { building.getBuildingType().translation(),
               Component.translatable("menu.building.residence.residents.count", occupants.size()), Component.empty(),
               Component.empty() };
      }
      return new Component[] { building.getBuildingType().translation(), Component.empty(), Component.empty(),
            Component.empty() };
   }
}
