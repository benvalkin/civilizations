package com.uncreated.civilized.ui.menu.building.worksite.grove.tabs;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.compress.utils.Lists;

import com.uncreated.civilized.core.building.Building;
import com.uncreated.civilized.core.building.state.GroveState;
import com.uncreated.civilized.core.building.util.BuildingUtil;
import com.uncreated.civilized.core.settlement.Settlement;
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

public class GroveInfoTab extends ABuildingScreenTab {

   private List<VillagerInfo> workers;
   private final Button chooseSaplings;

   public GroveInfoTab(
         int index,
         int x,
         int y,
         int width,
         int height,
         Font font,
         BuildingScreenContext context) {
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

      chooseSaplings =
            Button.builder(
                  Component.translatable("menu.building.worksite.grove.edit_allowed_saplings"),
                  // notice how if the button's 'x' is set to the parent's width, the button seems to render right to
                  // left...
                  // is this a feature??
                  this::onPressModifyItems).pos(x, height - 18).size(80, 18).build();
   }

   private void onPressModifyItems(Button button) {
      PacketDistributor.sendToServer(new RequestBuildingItemManagementScreen(context.building().getBuildingId(), 1));
   }

   protected Container createContainer() {
      return new SimpleContainer(1);
   }

   @Override
   protected List<Slot> createAndArrangeItemSlots(Container container) {

      GroveState groveBehaviour = (GroveState) context.building().getState();
      container.setItem(0, groveBehaviour.getSapling());

      ArrayList<Slot> slots = Lists.newArrayList();
      slots.add(new Slot(container, 0, getX() + 18, getHeight() - 60));
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

      chooseSaplings.render(graphics, mouseX, mouseY, partialTicks);

      graphics.drawString(
            font,
            Component.translatable("menu.building.worksite.grove.allowed_saplings.heading"),
            getX(),
            getHeight() - 70,
            Colors.MENU_TEXT_DARK,
            false);

      renderItems(graphics, mouseX, mouseY, partialTicks);
   }

   @Override
   public List<? extends GuiEventListener> children() {
      return List.of(chooseSaplings);
   }

   private List<VillagerInfo> createOccupantsList() {
      return BuildingUtil.getAssignedWorkers(context.building(), ClientVillagerStore.INSTANCE);
   }

   @Override
   public void refresh() {
      this.workers = createOccupantsList();
   }
}
