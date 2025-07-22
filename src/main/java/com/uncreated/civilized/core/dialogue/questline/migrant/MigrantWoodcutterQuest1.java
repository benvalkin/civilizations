package com.uncreated.civilized.core.dialogue.questline.migrant;

import static com.uncreated.civilized.core.dialogue.Dialogue.*;
import static com.uncreated.civilized.core.dialogue.DialogueWithPages.dialogueWithpages;
import static com.uncreated.civilized.core.dialogue.ResponseOption.option;
import static net.minecraft.network.chat.Component.translatable;

import com.uncreated.civilized.core.building.BuildingType;
import com.uncreated.civilized.core.dialogue.IVillageDialogue;
import com.uncreated.civilized.core.dialogue.context.DialogueContext;
import com.uncreated.civilized.core.dialogue.controller.DialogueFlow;
import com.uncreated.civilized.core.dialogue.questline.migrant.context.CanJoinSettlementCheck;
import com.uncreated.civilized.core.dialogue.questline.migrant.context.JoinSettlementAction;
import com.uncreated.civilized.core.dialogue.questline.migrant.context.JoinSettlementContext;
import com.uncreated.civilized.core.quest.attachments.PlayerQuests;
import com.uncreated.civilized.entity.CivilizedVillager;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;

public class MigrantWoodcutterQuest1 extends DialogueFlow {

   @Override
   public DialogueContext buildDialogueContext(CivilizedVillager villager, Player player, InteractionHand hand) {
      return new JoinSettlementContext(villager, player, BuildingType.WOODCUTTER_HOUSE);
   }

   @Override
   protected IVillageDialogue getOpeningDialogue(
         DialogueContext context,
         CivilizedVillager villager,
         Player player,
         PlayerQuests playerQuests) {
      return dialogueWithpages()
            .page(
                  simplePage(
                        translatable(
                              "villager.dialogue.quest.migrant_worker.woodcutter_1.page_1",
                              villager.getInfo().getFullName()),
                        translatable("villager.dialogue.quest.migrant_worker.woodcutter_1.page_1.response")))
            .page(
                  simplePage(
                        translatable("villager.dialogue.quest.migrant_worker.woodcutter_1.page_2"),
                        translatable("villager.dialogue.quest.migrant_worker.woodcutter_1.page_2.response")))
            .page(
                  simplePage(
                        translatable("villager.dialogue.quest.migrant_worker.woodcutter_1.page_3"),
                        translatable("villager.dialogue.quest.migrant_worker.woodcutter_1.page_3.response")))
            .page(simplePage(translatable("villager.dialogue.quest.migrant_worker.woodcutter_1.page_4")))
            .page(
                  dialogue(translatable("villager.dialogue.quest.migrant_worker.woodcutter_1.page_5")).response(
                        option(
                              translatable(
                                    "villager.dialogue.quest.migrant_worker.woodcutter_1.page_5.response.accept"))
                              .shouldBeEnabled(new CanJoinSettlementCheck())
                              .onSelectGoTo(
                                    dialogueWithpages().page(
                                          simplePage(
                                                translatable(
                                                      "villager.dialogue.quest.migrant_worker.woodcutter_1.accepted.page_1")))
                                          .page(
                                                finalPage(
                                                      translatable(
                                                            "villager.dialogue.quest.migrant_worker.woodcutter_1.accepted.page_2"))
                                                      .onEnded(new JoinSettlementAction()))
                                          .create()))
                        .response(
                              option(
                                    translatable(
                                          "villager.dialogue.quest.migrant_worker.woodcutter_1.page_5.response.reject"))))
            .create();
   }
}
