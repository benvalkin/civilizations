package com.uncreated.civilized.core.dialogue.questline.advisor;

import static com.uncreated.civilized.core.dialogue.Dialogue.*;
import static com.uncreated.civilized.core.dialogue.DialoguePackage.dialoguePackage;
import static com.uncreated.civilized.core.dialogue.DialoguePath.*;
import static com.uncreated.civilized.core.dialogue.DialogueWithPages.dialogueWithpages;
import static com.uncreated.civilized.core.dialogue.RandomSpeech.oneOf;
import static com.uncreated.civilized.core.dialogue.ResponseOption.closeDialogue;
import static com.uncreated.civilized.core.dialogue.ResponseOption.option;
import static com.uncreated.civilized.core.dialogue.actions.SoundActions.playerVillagerSound;
import static com.uncreated.civilized.core.dialogue.actions.quest.QuestActions.*;
import static com.uncreated.civilized.core.dialogue.actions.quest.QuestRequirements.*;
import static com.uncreated.civilized.core.dialogue.rewards.RewardActions.rewardCurrency;
import static com.uncreated.civilized.core.dialogue.specialized.ItemDepotDialogue.itemDepotDialogue;
import static net.minecraft.network.chat.Component.translatable;

import org.jetbrains.annotations.NotNull;

import com.uncreated.civilized.core.dialogue.Dialogue;
import com.uncreated.civilized.core.dialogue.DialoguePackage;
import com.uncreated.civilized.core.dialogue.specialized.ItemDepotDialogue;
import com.uncreated.civilized.core.quest.Quests;

import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;

public class AdvisorIntroQuest {
   public static DialoguePackage getPackage() {
      return dialoguePackage().addConditional(hasNotStartedQuest(Quests.ADVISOR_MEAL), questIntro())
            .addConditional(
                  hasActiveQuest(Quests.ADVISOR_MEAL),
                  Switch(Case(playerIsHoldingFood(), consumeFood()), defaultCase(questIncomplete())))
            .addConditional(hasCompletedQuest(Quests.ADVISOR_MEAL), questComplete());
   }

   private static Dialogue questIntro() {
      return dialogueWithpages()
            .page(
                  simplePage(
                        translatable("villager.dialogue.advisor.quest.join.1.page.1"),
                        translatable("villager.dialogue.advisor.quest.join.1.page.1.response")))
            .page(simplePage(translatable("villager.dialogue.advisor.quest.join.1.page.2")))
            .page(simplePage(translatable("villager.dialogue.advisor.quest.join.1.page.3")))
            .page(simplePage(translatable("villager.dialogue.advisor.quest.join.1.page.4")))
            .page(
                  dialogue(translatable("villager.dialogue.advisor.quest.join.1.page.5"))
                        .response(
                              option(translatable("villager.dialogue.advisor.quest.join.1.page.5.accept"))
                                    .onSelect(startQuest(Quests.ADVISOR_MEAL)))
                        .response(option(translatable("villager.dialogue.traveller.quest.stay_at_village.reject"))))
            .create();
   }

   private static Dialogue questComplete() {
      return dialogueWithpages()
            .page(simplePage(translatable("villager.dialogue.advisor.quest.join.complete.1.success.2")))
            .page(
                  finalPage(translatable("villager.dialogue.advisor.quest.join.complete.1.success.3"))
                        .onEnded(context -> {
                           endQuest(context, Quests.ADVISOR_MEAL);
                           rewardCurrency(context, 6);
                        }))
            .create();
   }

   private static Dialogue questIncomplete() {
      return dialogue(
            oneOf(
                  translatable("villager.dialogue.advisor.quest.join.complete.1.incomplete.1.o1"),
                  translatable("villager.dialogue.advisor.quest.join.complete.1.incomplete.1.o2"),
                  translatable("villager.dialogue.advisor.quest.join.complete.1.incomplete.1.o3"),
                  translatable("villager.dialogue.advisor.quest.join.complete.1.incomplete.1.o4")))
            .response(
                  closeDialogue().withTooltip(
                        Tooltip.create(
                              Component.translatable(
                                    "villager.dialogue.advisor.quest.join.complete.1.incomplete.1.response.disabled.tooltip"))));
   }

   private static ItemDepotDialogue consumeFood() {
      return itemDepotDialogue().consumeItemAction((context, itemStack, hand) -> {
         playerVillagerSound(context, SoundEvents.PLAYER_BURP);
         completeQuest(context, Quests.ADVISOR_MEAL);
         return itemStack.consumeAndReturn(1, context.getPlayer());
      });
   }

   private static @NotNull IDialogueAvailabilityCheck playerIsHoldingFood() {
      return context -> {
         ItemStack item = context.getPlayer().getMainHandItem();
         FoodProperties foodProperties = item.get(DataComponents.FOOD);
         return !item.isEmpty() && foodProperties != null && foodProperties.nutrition() >= 6;
      };
   }
}
