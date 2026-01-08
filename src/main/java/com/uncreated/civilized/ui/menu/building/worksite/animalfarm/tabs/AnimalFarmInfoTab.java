package com.uncreated.civilized.ui.menu.building.worksite.animalfarm.tabs;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.compress.utils.Lists;

import com.uncreated.civilized.core.building.state.AnimalFarmState;
import com.uncreated.civilized.core.building.util.BuildingUtil;
import com.uncreated.civilized.core.villagerinfo.ClientVillagerStore;
import com.uncreated.civilized.core.villagerinfo.VillagerInfo;
import com.uncreated.civilized.networking.packets.RequestBuildingItemManagementScreen;
import com.uncreated.civilized.ui.context.BuildingScreenContext;
import com.uncreated.civilized.ui.menu.building.ABuildingScreenTab;
import com.uncreated.civilized.ui.menu.building.worksite.tabs.ManageWorkersTab;
import com.uncreated.civilized.ui.style.Colors;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.inventory.Slot;
import net.neoforged.neoforge.network.PacketDistributor;

public class AnimalFarmInfoTab extends ABuildingScreenTab {

   private List<VillagerInfo> workers;
   private final Button chooseFood;

   public AnimalFarmInfoTab(int index, int x, int y, int width, int height, Font font, BuildingScreenContext context) {
      super(
            index,
            x,
            y,
            width,
            height,
            font,
            Component.translatable("menu.building.residence.info.tab.heading"),
            context);
      this.workers = createOccupantsList();

      chooseFood =
            Button.builder(
                  Component.translatable("menu.building.worksite.animal_farm.edit_allowed_animal_food"),
                  this::onPressModifyItems).pos(x, height - 18).size(80, 18).build();
   }

   private void onPressModifyItems(Button button) {
      PacketDistributor.sendToServer(new RequestBuildingItemManagementScreen(context.building().getBuildingId(), 3));
   }

   protected Container createContainer() {
      return new SimpleContainer(3);
   }

   @Override
   protected List<Slot> createAndArrangeItemSlots(Container container) {

      AnimalFarmState animalFarmBehaviour = (AnimalFarmState) context.building().getState();
      animalFarmBehaviour.tryApplyDefaults();
      container.setItem(0, animalFarmBehaviour.getFoodSlot(0));
      container.setItem(1, animalFarmBehaviour.getFoodSlot(1));
      container.setItem(2, animalFarmBehaviour.getFoodSlot(2));

      ArrayList<Slot> slots = Lists.newArrayList();
      for (int i = 0; i < container.getContainerSize(); i++) {
         slots.add(new Slot(container, i, getX() + 18 * i, getHeight() - 60));
      }
      return slots;
   }

   @Override
   public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
      super.renderWidget(graphics, mouseX, mouseY, partialTicks);

      graphics.drawString(
            font,
            Component.translatable(
                  "menu.building.worksite.workers.heading",
                  workers.size(),
                  ManageWorkersTab.MAX_ASSIGNED_WORKERS),
            getX(),
            getY() + 20,
            Colors.MENU_TEXT_DARK,
            false);

      for (int i = 0; i < workers.size(); i++) {

         VillagerInfo occupant = workers.get(i);
         graphics.drawString(
               font,
               Component.literal(occupant.getFullName()),
               getX() + 8,
               getY() + 35 + i * 10,
               Colors.MENU_TEXT_DARK,
               false);
      }

      chooseFood.render(graphics, mouseX, mouseY, partialTicks);

      graphics.drawString(
            font,
            Component.translatable("menu.building.worksite.animal_farm.allowed_animal_food.heading"),
            getX(),
            getHeight() - 70,
            Colors.MENU_TEXT_DARK,
            false);

      renderItems(graphics, mouseX, mouseY, partialTicks);
   }

   @Override
   public List<? extends GuiEventListener> children() {
      return List.of(chooseFood);
   }

   private List<VillagerInfo> createOccupantsList() {
      return BuildingUtil.getAssignedWorkers(context.building(), ClientVillagerStore.INSTANCE);
   }

   @Override
   public void refresh() {
      this.workers = createOccupantsList();
   }
}
