package com.uncreated.civilized.core.building.signs;

import static com.uncreated.civilized.ui.menu.building.worksite.tabs.ManageWorkersTab.MAX_ASSIGNED_WORKERS;

import java.util.Arrays;
import java.util.List;

import com.uncreated.civilized.core.building.Building;
import com.uncreated.civilized.core.building.BuildingTypeOld;
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

public class SignHelper {

   public static final String MARKER_TAG = "#building";

   public static SignText createSpecialSignText() {
      Component[] components =
            { Component.literal(MARKER_TAG), Component.empty(), Component.empty(), Component.empty() };
      return new SignText(components, components, DyeColor.BLACK, false);
   }

   public static void serverTriggerBuildingSignUpdate(SignBlockEntity entity) {
      entity.setText(createSpecialSignText(), true);
   }

   public static boolean signTextHasSpecialTag(SignText signText) {

      int blankCount = 0;
      boolean specialTagFound = false;
      Component[] messages = signText.getMessages(false);
      for (int i = 0; i < messages.length; i++) {
         Component component = messages[i];

         if (component.getString().isBlank())
            blankCount++;
         if (component.getString().equals(MARKER_TAG))
            specialTagFound = true;
      }

      return specialTagFound && blankCount == 3;
   }

   public static boolean signIsBlank(SignText signText) {
      return Arrays.stream(signText.getMessages(false)).allMatch(c -> c.getString().isBlank());
   }

   public static SignText getBuildingSignText(Building building) {
      Component[] signTextComponents = getSignTextComponents(building);
      return new SignText(signTextComponents, signTextComponents, DyeColor.BLACK, false);

   }

   private static Component[] getSignTextComponents(Building building) {
      if (building.getBuildingType() == BuildingTypeOld.TOWN_HALL) {
         Settlement settlement = ServerSettlementsStore.INSTANCE.get(building.getSettlementId());
         return new Component[] { building.getBuildingType().translation(),
               settlement.displayNameTranslation().withStyle(ChatFormatting.ITALIC), Component.empty(),
               Component.empty() };
      } else if (building.getBuildingType().isPermanentResidence()) {
         List<VillagerInfo> residents = BuildingUtil.getResidents(building, ServerVillagerStore.INSTANCE);
         return new Component[] { building.getBuildingType().translation(),
               Component.translatable("menu.building.residence.residents.count", residents.size()), Component.empty(),
               Component.empty() };
      } else if (building.getBuildingType().isTemporaryResidence()) {
         List<VillagerInfo> visitors = BuildingUtil.getResidents(building, ServerVillagerStore.INSTANCE);
         return new Component[] { building.getBuildingType().translation(),
               Component.translatable("menu.building.inn.visitors.count", visitors.size()), Component.empty(),
               Component.empty() };
      } else if (building.getBuildingType().isWorksite()) {
         List<VillagerInfo> workers = BuildingUtil.getAssignedWorkers(building, ServerVillagerStore.INSTANCE);
         return new Component[] { building.getBuildingType().translation(),
               Component.translatable("menu.building.worksite.workers.count", workers.size(), MAX_ASSIGNED_WORKERS),
               Component.empty(), Component.empty() };
      }
      return new Component[] { building.getBuildingType().translation(), Component.empty(), Component.empty(),
            Component.empty() };
   }
}
