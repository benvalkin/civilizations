package com.uncreated.civilized.ui.menu.building;

import static com.uncreated.civilized.CivilizedMod.CIVILIZED_MOD_ID;

import java.util.List;

import com.uncreated.civilized.core.building.Building;
import com.uncreated.civilized.core.building.BuildingType;
import com.uncreated.civilized.core.settlement.Settlement;
import com.uncreated.civilized.ui.menu.building.inn.InnBuildingScreen;
import com.uncreated.civilized.ui.menu.building.residence.ResidenceBuildingScreen;
import com.uncreated.civilized.ui.menu.building.worksite.WorksiteBuildingScreen;
import com.uncreated.civilized.ui.menu.building.worksite.cropfarm.CropFarmBuildingScreen;
import com.uncreated.civilized.ui.menu.building.worksite.grove.GroveBuildingScreen;
import com.uncreated.civilized.ui.tabs.AMenuScreenWithTabs;
import com.uncreated.civilized.ui.tabs.ATab;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public abstract class ABuildingScreen extends AMenuScreenWithTabs {
   private static final ResourceLocation MENU_TEXTURE =
         ResourceLocation.fromNamespaceAndPath(CIVILIZED_MOD_ID, "textures/gui/building_menu.png");

   protected final Building building;
   protected final Settlement settlement;
   protected int contentLeftPos;
   protected int contentTopPos;
   protected int contentWidth;
   protected int contentHeight;

   public ABuildingScreen(Building building, Settlement settlement, Component title) {
      super(title, 340, 200);
      this.building = building;
      this.settlement = settlement;
   }

   @Override
   protected final List<ATab> createTabs() {
      int marginX = 25;
      int marginY = 20;
      contentLeftPos = leftPos + marginX;
      contentTopPos = topPos + marginY;
      contentWidth = width - (width - imageWidth) - marginX * 2;
      contentHeight = height - (height - imageHeight) / 2 - marginY; // not sure why contentHeight doesn't need to be
                                                                     // multiplied
      // by 2...

      return createTabs(contentLeftPos, contentTopPos, contentWidth, contentHeight);
   }

   protected abstract List<ATab> createTabs(int contentLeftPos, int contentTopPos, int tabWidth, int tabHeight);

   protected abstract List<Button.Builder> createTabButtons();

   protected void init() {
      super.init();

      createTabButtons().forEach(builder -> addRenderableWidget(builder.build()));

      changeToDefaultTabIfNotSet();
   }

   @Override
   protected void renderBg(GuiGraphics graphics, float mouseX, int mouseY, int partialTicks) {
      int i = (this.width - this.imageWidth) / 2;
      int j = (this.height - this.imageHeight) / 2;
      graphics
            .blit(RenderType::guiTextured, MENU_TEXTURE, i, j, 0.0F, 0.0F, this.imageWidth, this.imageHeight, 384, 384);
   }

   public static ABuildingScreen factory(Building building, Settlement settlement) {

      BuildingType buildingType = building.getBuildingType();
      Component component = buildingType.translationDark().withStyle(ChatFormatting.UNDERLINE);

      if (buildingType == BuildingType.INN)
         return new InnBuildingScreen(building, settlement, component);
      if (buildingType.isPermanentResidence())
         return new ResidenceBuildingScreen(building, settlement, component);
      if (buildingType.isWorksite()) {

         if (buildingType == BuildingType.CROP_FARM)
            return new CropFarmBuildingScreen(building, settlement, component);
         if (buildingType == BuildingType.GROVE)
            return new GroveBuildingScreen(building, settlement, component);

         return new WorksiteBuildingScreen(building, settlement, component);
      }

      return new ResidenceBuildingScreen(building, settlement, component);
   }
}
