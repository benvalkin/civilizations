package com.uncreated.civilized.core.building.events;

import java.util.List;

import com.uncreated.civilized.CivilizedMod;
import com.uncreated.civilized.core.building.signs.SignHelper;
import com.uncreated.civilized.core.building.Building;
import com.uncreated.civilized.core.building.events.model.BuildingDeletedEvent;
import com.uncreated.civilized.core.building.events.model.BuildingUpdatedEvent;
import com.uncreated.civilized.neoforge.registration.attachments.DataAttachments;

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

      List<SignBlockEntity> signs =
            event.getBuilding()
                  .getBounds()
                  .getBlockEntitiesInsideBuilding(event.getLevel())
                  .stream()
                  .filter(b -> b instanceof SignBlockEntity)
                  .map(b -> (SignBlockEntity) b)
                  .toList();

      if (signs.isEmpty())
         return;

      SignBlockEntity primarySign = building.getPrimarySign(event.getLevel());
      if (primarySign == null) {

         // link primary sign if it has not been set yet (usually after creating a new building)
         primarySign = signs.stream().min((s1, s2) -> {
            if (SignHelper.signTextHasSpecialTag(s1.getFrontText()))
               return -2;
            if (SignHelper.signTextHasSpecialTag(s2.getFrontText()))
               return 2;
            if (SignHelper.signIsBlank(s1.getFrontText()))
               return -1;
            if (SignHelper.signIsBlank(s2.getFrontText()))
               return 1;
            return 0;
         }).orElseThrow();

         building.setPrimarySignPos(primarySign.getBlockPos());
         primarySign
               .setData(DataAttachments.LINKED_BUILDING, new DataAttachments.LinkedBuilding(building.getBuildingId()));
      }

      SignHelper.serverTriggerBuildingSignUpdate(primarySign);
   }

   @SubscribeEvent
   private static void onBuildingDeleted(BuildingDeletedEvent event) {

      if (event.isClientside() || event.getLevel() == null)
         return;

      SignBlockEntity sign = event.getBuilding().getPrimarySign(event.getLevel());
      if (sign == null)
         return;

      sign.setData(DataAttachments.LINKED_BUILDING, new DataAttachments.LinkedBuilding());
      sign.setText(new SignText(), true);
   }
}
