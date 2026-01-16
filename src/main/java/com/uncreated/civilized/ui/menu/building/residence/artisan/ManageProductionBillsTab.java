package com.uncreated.civilized.ui.menu.building.residence.artisan;

import java.util.List;

import javax.annotation.Nullable;

import org.apache.commons.compress.utils.Lists;

import com.uncreated.civilized.core.StoreOperation;
import com.uncreated.civilized.core.building.ClientBuildingStore;
import com.uncreated.civilized.core.building.crafting.bills.ProductionBill;
import com.uncreated.civilized.core.building.state.ArtisanHouseState;
import com.uncreated.civilized.ui.components.ScrollListView;
import com.uncreated.civilized.ui.context.BuildingScreenContext;
import com.uncreated.civilized.ui.menu.building.ABuildingScreenTab;
import com.uncreated.civilized.ui.menu.building.widgets.ProductionBillListViewWidget;
import com.uncreated.civilized.ui.style.Colors;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.network.chat.Component;

public class ManageProductionBillsTab extends ABuildingScreenTab {

   public static final int MAX_ASSIGNED_RESIDENTS = 2;

   @Nullable
   private ScrollListView scrollView;

   public ManageProductionBillsTab(
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
            Component.translatable("menu.building.residence.production_bills.tab.heading"),
            context);
      refresh();
   }

   @Override
   public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
      super.renderWidget(graphics, mouseX, mouseY, partialTicks);

      if (scrollView != null)
         scrollView.render(graphics, mouseX, mouseY, partialTicks);
      else
         graphics.drawWordWrap(
               font,
               Component.translatable("menu.building.residence.production_bills.heading.empty"),
               getX(),
               getY() + 15,
               width,
               Colors.MENU_TEXT_DARK,
               false);
   }

   @Override
   public List<? extends GuiEventListener> children() {
      List<GuiEventListener> children = Lists.newArrayList();
      children.add(scrollView);
      children.addAll(scrollView.children());
      return children;
   }

   private ScrollListView createScrollView(List<ProductionBill> productionBills) {
      return new ScrollListView(getX(), getY() + 30, width, height, (x_, y_, w, h) -> {
         List<AbstractWidget> elements = Lists.newArrayList();

         final int elementHeight = 25;

         int elementIndex = 0;
         for (ProductionBill bill : productionBills) {

            elements.add(
                  new ProductionBillListViewWidget(
                        x_,
                        y_ + elementIndex * elementHeight,
                        w - 10,
                        elementHeight,
                        font,
                        bill,
                        elementIndex,
                        context.building()));

            elementIndex++;
         }

         return elements;
      });
   }

   @Override
   public void refresh() {

      if (context.building().getState() instanceof ArtisanHouseState state) {

         state.tryLoadDefaultProductionBills();
         ClientBuildingStore.INSTANCE.replicateChange(context.building(), StoreOperation.UPDATE);

         scrollView = createScrollView(state.getProductionBills());
      }
   }
}
