package com.uncreated.civilized.ui;

import static com.uncreated.civilized.CivilizedMod.CIVILIZED_MOD_ID;

import com.uncreated.civilized.core.settlement.Settlement;
import com.uncreated.civilized.networking.packets.UpdateSettlementInfo;
import com.mojang.logging.LogUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.FittingMultiLineTextWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class SettlementScreen extends Screen {
   private final Button buttonConfirm;
   private final FittingMultiLineTextWidget textSettlementName;
   private final EditBox editBoxName;
   @org.jetbrains.annotations.NotNull
   private final Settlement settlement;

   public SettlementScreen(Component title, Settlement settlement) {
      super(title);
      this.settlement = settlement;

      font = Minecraft.getInstance().font;

      int OFFSET_X = 64;

      textSettlementName =
            new FittingMultiLineTextWidget(OFFSET_X, 64, 240, 64, Component.literal(settlement.getDisplayName()), font);
      addRenderableOnly(textSettlementName);

      editBoxName = new EditBox(this.font, OFFSET_X, 140, 160, 16, Component.literal("wallahi"));
      editBoxName.setValue(settlement.getDisplayName());

      buttonConfirm =
              Button.builder(Component.literal("Save Settlement Name"), this::onClickConfirm)
                      .pos(OFFSET_X, 180)
                      .size(150, 18)
                      .tooltip(Tooltip.create(Component.literal(settlement.getSettlementId().toString())))
                      .build();
      addRenderableWidget(buttonConfirm);
      addRenderableWidget(editBoxName);
   }

   @Override
   public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
      // Background is typically rendered first
      this.renderBackground(graphics, mouseX, mouseY, partialTick);

      // Render things here before widgets (background textures)

      // Then the widgets if this is a direct child of the Screen
      super.render(graphics, mouseX, mouseY, partialTick);

      // Render things after widgets (tooltips)
   }

   private static final ResourceLocation BACKGROUND_LOCATION =
         ResourceLocation.fromNamespaceAndPath(CIVILIZED_MOD_ID, "textures/gui/container/my_container_screen.png");

   // In some Screen subclass

   @Override
   public void onClose() {
      // Stop any handlers here

      // Call last in case it interferes with the override
      super.onClose();
   }

   @Override
   public void removed() {
      // Reset initial states here

      if (!settlement.getDisplayName().equals(editBoxName.getValue())) {
         PacketDistributor.sendToServer(new UpdateSettlementInfo(settlement.getSettlementId(), editBoxName.getValue()));
      }

      // Call last in case it interferes with the override
      super.removed();
   }

   private void onClickConfirm(Button button) {
      Minecraft.getInstance().setScreen(null);
   }

   public static void clientReceiveSettlementMenuRsp(final Settlement settlement, final IPayloadContext context) {
      LogUtils.getLogger().info("Received Settlement response: {}", settlement.getSettlementId());

      Minecraft.getInstance().setScreen(new SettlementScreen(Component.literal(settlement.getDisplayName()), settlement));
   }
}
