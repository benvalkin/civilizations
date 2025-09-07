package com.uncreated.civilized.ui.menu.building.worksite.grove.items;

import static com.uncreated.civilized.CivilizedMod.CIVILIZED_MOD_ID;

import com.uncreated.civilized.ui.menu.building.item.management.ItemManagementScreen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class ChooseSaplingsScreen extends ItemManagementScreen<ChooseSaplingsMenu> {
   private static final ResourceLocation MENU_TEXTURE =
         ResourceLocation.fromNamespaceAndPath(CIVILIZED_MOD_ID, "textures/gui/container/grove_choose_saplings.png");

   private Button done;

   public ChooseSaplingsScreen(ChooseSaplingsMenu menu, Inventory playerInventory, Component title) {
      super(menu, playerInventory, title);
   }

   @Override
   protected void init() {
      super.init();
      int contentWidth = width - (width - imageWidth) / 2;
      int contentHeight = height - (height - imageHeight) / 2;
      done =
            Button.builder(Component.translatable("gui.misc.button.done"), this::onPressDone)
                  .pos(contentWidth - 85, contentHeight - 40)
                  .size(60, 18)
                  .build();

      addRenderableWidget(done);
   }

   private void onPressDone(Button button) {
      Minecraft.getInstance().player.closeContainer(); // for some reason, menu.stopOpen doesn't sync to server
   }

   public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
      super.render(graphics, mouseX, mouseY, partialTicks);
      this.renderTooltip(graphics, mouseX, mouseY);
   }

   protected void renderBg(GuiGraphics p_281616_, float p_282737_, int p_281678_, int p_281465_) {
      int i = (this.width - this.imageWidth) / 2;
      int j = (this.height - this.imageHeight) / 2;
      p_281616_
            .blit(RenderType::guiTextured, MENU_TEXTURE, i, j, 0.0F, 0.0F, this.imageWidth, this.imageHeight, 384, 384);
   }
}
