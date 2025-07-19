package com.uncreated.civilized.core.dialogue.questline.traveler;

import static com.uncreated.civilized.core.dialogue.Dialogue.*;
import static com.uncreated.civilized.core.dialogue.DialoguePackage.dialoguePackage;
import static com.uncreated.civilized.core.dialogue.DialogueWithPages.dialogueWithpages;
import static com.uncreated.civilized.core.dialogue.RandomSpeech.randomPlayerGreeting;
import static com.uncreated.civilized.core.dialogue.ResponseOption.option;
import static net.minecraft.network.chat.Component.translatable;

import java.util.Optional;

import com.uncreated.civilized.core.StoreOperation;
import com.uncreated.civilized.core.building.Building;
import com.uncreated.civilized.core.building.ClientBuildingStore;
import com.uncreated.civilized.core.building.util.BuildingUtil;
import com.uncreated.civilized.core.dialogue.DialoguePackage;
import com.uncreated.civilized.core.dialogue.ResponseOption;
import com.uncreated.civilized.core.dialogue.context.ResponseOptionContext;
import com.uncreated.civilized.core.settlement.ClientSettlementsStore;
import com.uncreated.civilized.core.settlement.Settlement;
import com.uncreated.civilized.core.villagerinfo.ClientVillagerStore;
import com.uncreated.civilized.core.villagerinfo.VillagerInfo;
import com.uncreated.civilized.ui.style.Colors;

import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.world.entity.player.Player;

public class JoinSettlementQuest {

   public static DialoguePackage getPackage() {
      return dialoguePackage().add(
            dialogueWithpages()
                  .page(simplePage(translatable("villager.dialogue.traveller.quest.stay_at_village.1.page.1"), randomPlayerGreeting()))
                  .page(simplePage(translatable("villager.dialogue.traveller.quest.stay_at_village.1.page.2")))
                  .page(
                        dialogue(translatable("villager.dialogue.traveller.quest.stay_at_village.1.page.3"))
                              .response(
                                    option(translatable("villager.dialogue.traveller.quest.stay_at_village.accept"))
                                          .shouldBeEnabled(new CanJoinSettlementCheck())
                                          .onSelect(new VillagerJoinSettlement()))
                              .response(
                                    option(translatable("villager.dialogue.traveller.quest.stay_at_village.reject"))
                                          .onSelectGoTo(
                                                finalPage(
                                                      translatable(
                                                            "villager.dialogue.traveller.quest.stay_at_village.reject.page.1")))))
                  .create());
   }

   private static class VillagerJoinSettlement implements ResponseOption.IOnResponseSelectedAction {
      @Override
      public ResponseOption.SelectedAction onOptionSelected(ResponseOptionContext selectedContext) {

         JoinSettlementContext joinSettlement = selectedContext.getDialogueContext().as();

         Settlement settlement = joinSettlement.getSettlement();
         VillagerInfo info = joinSettlement.getVillager().getInfo();

         joinSettlement.getVillager().getInfo().setSettlementId(settlement.getSettlementId());
         ClientVillagerStore.INSTANCE.replicateChange(info, StoreOperation.UPDATE);
         ClientVillagerStore.INSTANCE.setDirty();

         joinSettlement.getPlayer()
               .displayClientMessage(
                     translatable("villager.dialogue.traveller.quest.stay_at_village.complete", info.getFullName())
                           .withColor(Colors.VALIDATION_SUCCESS),
                     false);
         return ResponseOption.SelectedAction.CLOSE_DIALOGUE;
      }
   }

   private static class CanJoinSettlementCheck implements ResponseOption.IResponseOptionEnabledCheck {

      @Override
      public ResponseOption.EnabledCheckResult isOptionEnabled(ResponseOptionContext context) {

         JoinSettlementContext joinSettlement = context.getDialogueContext().as();

         VillagerInfo info = joinSettlement.getVillager().getInfo();
         Player player = joinSettlement.getPlayer();
         if (info.getSettlementId() != null)
            return ResponseOption.EnabledCheckResult.failed(
                  Tooltip.create(
                        translatable(
                              "villager.dialogue.traveller.quest.validation.tooltip.villager_already_has_settlement")
                              .withColor(Colors.VALIDATION_ERROR)));

         Optional<Settlement> ownerSettlement = ClientSettlementsStore.INSTANCE.findFromOwner(player.getUUID());
         if (ownerSettlement.isEmpty()) {
            return ResponseOption.EnabledCheckResult.failed(
                  Tooltip.create(
                        translatable("villager.dialogue.traveller.quest.validation.tooltip.player_no_settlement")
                              .withColor(Colors.VALIDATION_ERROR)));
         }

         Optional<Building> unoccupiedBuilding =
               BuildingUtil.findUnoccupiedHome(
                     ownerSettlement.get().getSettlementId(),
                     ClientBuildingStore.INSTANCE,
                     ClientVillagerStore.INSTANCE);

         if (unoccupiedBuilding.isEmpty()) {
            return ResponseOption.EnabledCheckResult.failed(
                  Tooltip.create(
                        translatable("villager.dialogue.traveller.quest.validation.tooltip.no_available_buildings")
                              .withColor(Colors.VALIDATION_ERROR)));
         }

         joinSettlement.setSettlement(ownerSettlement.get());
         joinSettlement.setUnoccupiedBuilding(unoccupiedBuilding.get());
         return ResponseOption.EnabledCheckResult.success();
      }
   }
}
