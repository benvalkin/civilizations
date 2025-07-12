package com.uncreated.civilized.block.building.signs;

import java.util.Arrays;
import java.util.List;

import com.uncreated.civilized.core.building.Building;
import com.uncreated.civilized.core.building.BuildingType;
import com.uncreated.civilized.core.building.util.BuildingUtil;
import com.uncreated.civilized.core.settlement.ServerSettlementsStore;
import com.uncreated.civilized.core.settlement.Settlement;
import com.uncreated.civilized.core.villagerinfo.ServerVillagerStore;
import com.uncreated.civilized.core.villagerinfo.VillagerInfo;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.entity.SignText;

public class SignHelper {

   public static final String MARKER_TAG = "#building";

   private static final SignText createSpecialSignText() {
      Component[] components =
            { Component.literal(MARKER_TAG), Component.empty(), Component.empty(), Component.empty() };
      return new SignText(components, components, DyeColor.BLACK, false);
   }

   public static final SignText SPECIAL_BUILDING_MARKER_SIGN_TEXT = createSpecialSignText();

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
