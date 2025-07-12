package com.uncreated.civilized.ui.events;

import com.uncreated.civilized.CivilizedMod;
import com.uncreated.civilized.core.villagerinfo.events.model.VillagerInfoUpdatedEvent;
import com.uncreated.civilized.ui.menu.building.ABuildingScreen;

import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber(value = Dist.CLIENT, modid = CivilizedMod.CIVILIZED_MOD_ID)
public class UIUpdateEvents {
   @SubscribeEvent
   public static void onVillagerInfoUpdated(VillagerInfoUpdatedEvent event) {
      if (!event.isClientside())
         return;

      updateScreen();
   }

   private static void updateScreen() {
      if (Minecraft.getInstance().screen instanceof ABuildingScreen buildingScreen) {
         buildingScreen.refresh();
      }
   }
}
