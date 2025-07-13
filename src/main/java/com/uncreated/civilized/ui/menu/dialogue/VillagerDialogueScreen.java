package com.uncreated.civilized.ui.menu.dialogue;

import static com.uncreated.civilized.CivilizedMod.CIVILIZED_MOD_ID;

import java.util.List;

import org.apache.commons.compress.utils.Lists;

import com.uncreated.civilized.core.dialogue.DialogueWithPages;
import com.uncreated.civilized.core.dialogue.IVillageDialogue;
import com.uncreated.civilized.core.dialogue.ResponseOption;
import com.uncreated.civilized.core.dialogue.context.DialogueContext;
import com.uncreated.civilized.core.dialogue.context.ResponseOptionContext;
import com.uncreated.civilized.entity.CivilizedVillager;
import com.uncreated.civilized.ui.components.buttons.ModernButton;
import com.uncreated.civilized.ui.components.multiline.ImprovedMultiLineTextWidget;
import com.uncreated.civilized.ui.style.Colors;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;

public class VillagerDialogueScreen extends Screen {
   private static final ResourceLocation BACKGROUND_TEXTURE =
         ResourceLocation.fromNamespaceAndPath(CIVILIZED_MOD_ID, "textures/gui/villager_dialogue.png");

   private final int imageWidth;
   private final int imageHeight;
   private int leftPos;
   private int topPos;
   private int contentWidth;
   private int contentHeight;
   private int contentMarginX = 120;
   private int contentMarginY = 150;
   private int titleX;
   private int titleY;

   private ImprovedMultiLineTextWidget villagerSpeechBox;
   private List<Button> responseButtons;

   private final CivilizedVillager villager;
   private final IVillageDialogue dialogue;
   private final DialogueContext context;

   public VillagerDialogueScreen(CivilizedVillager villager, IVillageDialogue dialogue, DialogueContext context) {
      super(Component.translatable(villager.getInfo().getFullName()));
      this.villager = villager;
      this.dialogue = dialogue;
      this.context = context;
      imageWidth = 512;
      imageHeight = 512;
   }

   @Override
   public boolean isPauseScreen() {
      return false;
   }

   protected void init() {
      super.init();
      this.leftPos = (width - this.imageWidth) / 2 + contentMarginX;
      this.topPos = (height - this.imageHeight) / 2 + contentMarginY;
      this.contentWidth = imageWidth - contentMarginX * 2;
      this.contentHeight = imageHeight - 125;
      this.titleX = leftPos;
      this.titleY = topPos;

      villagerSpeechBox = new ImprovedMultiLineTextWidget(leftPos, topPos + 20, dialogue.getVillagerSpeech(), font);
      villagerSpeechBox.setCentered(true);
      villagerSpeechBox.setMaxWidth(contentWidth);
      villagerSpeechBox.setColor(Colors.MENU_TEXT_VILLAGER_DIALOGUE);
      addRenderableOnly(villagerSpeechBox);

      final int buttonStartY = 100;
      final int buttonMarginX = 20;
      final int buttonHeight = 16;

      responseButtons = Lists.newArrayList();
      int index = 0;
      for (ResponseOption responseOption : dialogue.getResponseOptions()) {
         ResponseOptionContext optionContext = new ResponseOptionContext(responseOption, context);

         boolean shouldShow = responseOption.getVisibleCheck().isOptionVisible(optionContext);
         if (!shouldShow)
            continue;

         Button button =
               new ModernButton(
                     Button.builder(responseOption.getPlayerSpeech(), b -> onResponseOptionPressed(b, optionContext))
                           .pos(leftPos + buttonMarginX, topPos + buttonStartY + index * buttonHeight)
                           .size(contentWidth - buttonMarginX * 2, buttonHeight));

         ResponseOption.EnabledCheckResult enabledResult =
               responseOption.getEnabledCheck().isOptionEnabled(optionContext);
         button.active = enabledResult.isEnabled();
         button.setTooltip(enabledResult.getTooltip());

         responseButtons.add(button);
         addRenderableWidget(button);
         index++;
      }
   }

   private void onResponseOptionPressed(Button button, ResponseOptionContext optionContext) {
      ResponseOption.DialogueAction action =
            optionContext.getSelectedOption().getOnPress().onOptionSelected(optionContext);
      if (action == ResponseOption.DialogueAction.CLOSE_DIALOGUE)
         Minecraft.getInstance().setScreen(null);
      else if (action == ResponseOption.DialogueAction.NEXT_PAGE && dialogue instanceof DialogueWithPages withPages) {
         withPages.goNextPage();
         rebuildWidgets();
      }
   }

   public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
      super.render(graphics, mouseX, mouseY, partialTicks);
      // this.renderBackground(graphics, mouseX, mouseY, partialTicks);
      responseButtons.forEach(b -> b.render(graphics, mouseX, mouseY, partialTicks));
      this.renderLabels(graphics, mouseX, mouseY, partialTicks);
   }

   protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
      MutableComponent villagerName =
            Component.literal(villager.getInfo().getFullName()).withStyle(ChatFormatting.UNDERLINE);
      graphics.drawCenteredString(
            this.font,
            villagerName,
            leftPos + contentWidth / 2,
            topPos,
            Colors.MENU_TEXT_VILLAGER_DIALOGUE);
   }

   public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
      int i = (this.width - this.imageWidth) / 2;
      int j = (this.height - this.imageHeight) / 2;
      graphics.blit(
            RenderType::guiTextured,
            BACKGROUND_TEXTURE,
            i,
            j,
            0.0F,
            0.0F,
            this.imageWidth,
            this.imageHeight,
            512,
            512);
   }
}
