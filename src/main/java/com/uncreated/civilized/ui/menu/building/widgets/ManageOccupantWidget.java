package com.uncreated.civilized.ui.menu.building.widgets;

import java.util.List;

import com.uncreated.civilized.core.StoreOperation;
import com.uncreated.civilized.core.building.Building;
import com.uncreated.civilized.core.building.ClientBuildingStore;
import com.uncreated.civilized.core.settlement.ServerSettlementsStore;
import com.uncreated.civilized.core.villagerinfo.ClientVillagerStore;
import com.uncreated.civilized.core.villagerinfo.VillagerInfo;
import com.uncreated.civilized.entity.VillagerOccupation;
import com.uncreated.civilized.ui.style.Colors;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractContainerWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

public class ManageOccupantWidget extends AbstractContainerWidget {

   private final Font font;
   private final Building building;
   private final VillagerInfo villagerInfo;
   private final Button button;
   private EManagementOption option;
   private final boolean isBuildingFull;

   public ManageOccupantWidget(
         int x,
         int y,
         int width,
         int height,
         Font font,
         Building building,
         VillagerInfo villagerInfo,
         EManagementOption option,
         boolean isBuildingFull) {
      super(x, y, width, height, Component.literal("ManageOccupantWidget"));
      this.font = font;
      this.building = building;
      this.villagerInfo = villagerInfo;
      this.option = option;
      this.isBuildingFull = isBuildingFull;

      button = Button.builder(getButtonText(option), this::onPress).pos(x + width - 40, y + 10).size(40, 12).build();
   }

   private Component getButtonText(EManagementOption option) {
      return Component.literal((switch (option) {
      case ASSIGN -> "Assign";
      case EVICT -> "Evict";
      default -> "N/A";
      }));
   }

   @Override
   protected int contentHeight() {
      return height;
   }

   @Override
   protected double scrollRate() {
      return 0;
   }

   @Override
   protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
      guiGraphics.drawString(font, villagerInfo.getFullName(), getX(), getY(), Colors.MENU_TEXT_DARK, false);

      button.active = option == EManagementOption.EVICT || !isBuildingFull;
      button.render(guiGraphics, mouseX, mouseY, partialTick);
   }

   @Override
   protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

   }

   @Override
   public List<? extends GuiEventListener> children() {
      return List.of(button);
   }

   private void onPress(Button b) {
      ServerSettlementsStore ClientBuildingsStore;
      if (option == EManagementOption.ASSIGN) {
         villagerInfo.setHomeBuildingId(building.getBuildingId());
         villagerInfo.setOccupation(building.getBuildingType().toJobType());
         ClientVillagerStore.INSTANCE.replicateChange(villagerInfo, StoreOperation.UPDATE);
         ClientBuildingStore.INSTANCE.replicateChange(building, StoreOperation.UPDATE);
      } else if (option == EManagementOption.EVICT) {
         villagerInfo.setHomeBuildingId(null);
         villagerInfo.setOccupation(VillagerOccupation.UNEMPLOYED);
         ClientVillagerStore.INSTANCE.replicateChange(villagerInfo, StoreOperation.UPDATE);
         ClientBuildingStore.INSTANCE.replicateChange(building, StoreOperation.UPDATE);
      }
   }

   public enum EManagementOption {
      ASSIGN, EVICT
   }
}
