package com.uncreated.civilized.ui.menu.building.widgets;

import com.uncreated.civilized.core.StoreOperation;
import com.uncreated.civilized.core.building.Building;
import com.uncreated.civilized.core.building.ClientBuildingStore;
import com.uncreated.civilized.core.villagerinfo.ClientVillagerStore;
import com.uncreated.civilized.core.villagerinfo.VillagerInfo;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

public class ManageWorkerWidget extends ManageOccupantWidget {

   public ManageWorkerWidget(
         int x,
         int y,
         int width,
         int height,
         Font font,
         Building building,
         VillagerInfo villagerInfo,
         ManagementOption option,
         boolean isBuildingFull) {
      super(x, y, width, height, font, building, villagerInfo, option, isBuildingFull);
   }

   @Override
   protected Component getAssignButtonTranslation() {
      return Component.translatable("menu.building.worksite.workers.button.assign_worker");
   }

   @Override
   protected Component getUnassignButtonTranslation() {
      return Component.translatable("menu.building.worksite.workers.button.unassign_worker");
   }

   @Override
   protected void onPressedAssign(Button button, VillagerInfo villagerInfo, Building building) {
      villagerInfo.setPrimaryWorksiteId(building.getBuildingId());
      ClientVillagerStore.INSTANCE.replicateChange(villagerInfo, StoreOperation.UPDATE);
      ClientBuildingStore.INSTANCE.replicateChange(building, StoreOperation.UPDATE);
   }

   @Override
   protected void onPressedUnassign(Button button, VillagerInfo villagerInfo, Building building) {
      villagerInfo.setPrimaryWorksiteId(null);
      ClientVillagerStore.INSTANCE.replicateChange(villagerInfo, StoreOperation.UPDATE);
      ClientBuildingStore.INSTANCE.replicateChange(building, StoreOperation.UPDATE);
   }
}
