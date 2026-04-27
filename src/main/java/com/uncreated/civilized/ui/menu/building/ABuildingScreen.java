package com.uncreated.civilized.ui.menu.building;

import static com.uncreated.civilized.CivilizedMod.CIVILIZED_MOD_ID;

import java.util.List;

import com.uncreated.civilized.core.building.Building;
import com.uncreated.civilized.core.building.BuildingType;
import com.uncreated.civilized.core.settlement.Settlement;
import com.uncreated.civilized.ui.context.BuildingScreenContext;
import com.uncreated.civilized.ui.menu.building.inn.InnBuildingScreen;
import com.uncreated.civilized.ui.menu.building.residence.ResidenceBuildingScreen;
import com.uncreated.civilized.ui.menu.building.residence.artisan.BakeryBuildingScreen;
import com.uncreated.civilized.ui.menu.building.residence.artisan.BlacksmithBuildingScreen;
import com.uncreated.civilized.ui.menu.building.residence.artisan.ButcheryBuildingScreen;
import com.uncreated.civilized.ui.menu.building.residence.artisan.CraftsmanHouseBuildingScreen;
import com.uncreated.civilized.ui.menu.building.residence.artisan.MasonBuildingScreen;
import com.uncreated.civilized.ui.menu.building.worksite.WorksiteBuildingScreen;
import com.uncreated.civilized.ui.menu.building.worksite.animalfarm.AnimalFarmBuildingScreen;
import com.uncreated.civilized.ui.menu.building.worksite.cropfarm.CropFarmBuildingScreen;
import com.uncreated.civilized.ui.menu.building.worksite.grove.GroveBuildingScreen;
import com.uncreated.civilized.ui.tabs.AScreenWithTabs;
import com.uncreated.civilized.ui.tabs.ATab;

import lombok.Getter;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public abstract class ABuildingScreen extends AScreenWithTabs {
   private static final ResourceLocation MENU_TEXTURE =
         ResourceLocation.fromNamespaceAndPath(CIVILIZED_MOD_ID, "textures/gui/building_menu.png");

   @Getter
   protected final BuildingScreenContext context;
   protected int contentLeftPos;
   protected int contentTopPos;
   protected int contentWidth;
   protected int contentHeight;

   public ABuildingScreen(BuildingScreenContext context, Component title) {
      super(title, 340, 200);
      this.context = context;
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

   protected abstract List<Button> createTabButtons();

   protected void init() {
      super.init();

      createTabButtons().forEach(this::addRenderableWidget);

      changeToDefaultTabIfNotSet();
   }

   @Override
   public int getFirstTabButtonX() {
      return leftPos - 12;
   }

   @Override
   public int getFirstTabButtonY() {
      return topPos + 14;
   }

   @Override
   protected void renderBg(GuiGraphics graphics, float mouseX, int mouseY, int partialTicks) {
      int i = (this.width - this.imageWidth) / 2;
      int j = (this.height - this.imageHeight) / 2;
      graphics
            .blit(RenderType::guiTextured, MENU_TEXTURE, i, j, 0.0F, 0.0F, this.imageWidth, this.imageHeight, 384, 384);
   }

   public static ABuildingScreen factory(Building building, Settlement settlement, BuildingScreenContext context) {

      BuildingType buildingType = building.getBuildingType();
      Component component = buildingType.translationDark().withStyle(ChatFormatting.UNDERLINE);

      if (buildingType == BuildingType.INN)
         return new InnBuildingScreen(context, component);
      if (buildingType.isArtisanBuilding()) {
         if (buildingType == BuildingType.BAKER_HOUSE)
            return new BakeryBuildingScreen(context, component);
         else if (buildingType == BuildingType.BUTCHER_HOUSE)
            return new ButcheryBuildingScreen(context, component);
         else if (buildingType == BuildingType.BLACKSMITH_HOUSE)
            return new BlacksmithBuildingScreen(context, component);
         else if (buildingType == BuildingType.MASON_HOUSE)
            return new MasonBuildingScreen(context, component);
         else
            return new CraftsmanHouseBuildingScreen(context, component);
      }
      if (buildingType.isPermanentResidence())
         return new ResidenceBuildingScreen(context, component);
      if (buildingType.isWorksite()) {

         if (buildingType == BuildingType.CROP_FARM)
            return new CropFarmBuildingScreen(context, component);
         if (buildingType == BuildingType.GROVE)
            return new GroveBuildingScreen(context, component);
         if (buildingType.isAnimalFarm())
            return new AnimalFarmBuildingScreen(context, component);

         return new WorksiteBuildingScreen(context, component);
      }

      return new ResidenceBuildingScreen(context, component);
   }
}
