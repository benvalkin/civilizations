package com.uncreated.civilized.core.dialogue.traveler;

import java.util.Optional;

import com.uncreated.civilized.core.StoreOperation;
import com.uncreated.civilized.core.building.Building;
import com.uncreated.civilized.core.building.ClientBuildingStore;
import com.uncreated.civilized.core.building.util.BuildingUtil;
import com.uncreated.civilized.core.dialogue.Dialogue;
import com.uncreated.civilized.core.dialogue.DialoguePackage;
import com.uncreated.civilized.core.dialogue.DialogueWithPages;
import com.uncreated.civilized.core.dialogue.ResponseOption;
import com.uncreated.civilized.core.dialogue.context.ResponseOptionContext;
import com.uncreated.civilized.core.dialogue.traveler.quest.JoinSettlementContext;
import com.uncreated.civilized.core.settlement.ClientSettlementsStore;
import com.uncreated.civilized.core.settlement.Settlement;
import com.uncreated.civilized.core.villagerinfo.ClientVillagerStore;
import com.uncreated.civilized.core.villagerinfo.VillagerInfo;
import com.uncreated.civilized.ui.style.Colors;

import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

public class QuestStayAtVillage {

   public static DialoguePackage questStayAtVillage() {
      return DialoguePackage.create()
            .add(
                  DialogueWithPages.create("traveler_1")
                        .page(
                              Dialogue.simplePage(
                                    Component.translatable("villager.dialogue.traveller.1.page.1"),
                                    Component.translatable("villager.dialogue.response.misc.greet.1")))
                        .page(Dialogue.simplePage(Component.translatable("villager.dialogue.traveller.1.page.2")))
                        .page(
                              Dialogue.create("quest.stay_at_village")
                                    .villagerSpeech(Component.translatable("villager.dialogue.traveller.1.page.3"))
                                    .response(
                                          ResponseOption
                                                .create(
                                                      "quest.stay_at_village.accept",
                                                      Component.translatable(
                                                            "villager.dialogue.traveller.quest.stay_at_village.accept"))
                                                .shouldBeEnabled(new CanJoinSettlementCheck())
                                                .onSelect(new VillagerJoinSettlement()))
                                    .response(
                                          ResponseOption
                                                .create(
                                                      "quest.stay_at_village.reject",
                                                      Component.translatable(
                                                            "villager.dialogue.traveller.quest.stay_at_village.reject"))
                                                .onSelectCloseDialogue())
                                    .build())
                        .build());
   }

   private static class VillagerJoinSettlement implements ResponseOption.IOnResponseSelectedAction {
      @Override
      public ResponseOption.DialogueAction onOptionSelected(ResponseOptionContext selectedContext) {

         JoinSettlementContext joinSettlement = selectedContext.getDialogueContext().as();

         Settlement settlement = joinSettlement.getSettlement();
         VillagerInfo info = joinSettlement.getVillager().getInfo();

         joinSettlement.getVillager().getInfo().setSettlementId(settlement.getSettlementId());
         ClientVillagerStore.INSTANCE.replicateChange(info, StoreOperation.UPDATE);
         ClientVillagerStore.INSTANCE.setDirty();

         joinSettlement.getPlayer()
               .displayClientMessage(
                     Component
                           .translatable(
                                 "villager.dialogue.traveller.quest.stay_at_village.complete",
                                 info.getFullName())
                           .withColor(Colors.VALIDATION_SUCCESS),
                     false);
         return ResponseOption.DialogueAction.CLOSE_DIALOGUE;
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
                        Component.translatable(
                              "villager.dialogue.traveller.quest.validation.tooltip.villager_already_has_settlement")
                              .withColor(Colors.VALIDATION_ERROR)));

         Optional<Settlement> ownerSettlement = ClientSettlementsStore.INSTANCE.findFromOwner(player.getUUID());
         if (ownerSettlement.isEmpty()) {
            return ResponseOption.EnabledCheckResult.failed(
                  Tooltip.create(
                        Component
                              .translatable("villager.dialogue.traveller.quest.validation.tooltip.player_no_settlement")
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
                        Component
                              .translatable(
                                    "villager.dialogue.traveller.quest.validation.tooltip.no_available_buildings")
                              .withColor(Colors.VALIDATION_ERROR)));
         }

         joinSettlement.setSettlement(ownerSettlement.get());
         joinSettlement.setUnoccupiedBuilding(unoccupiedBuilding.get());
         return ResponseOption.EnabledCheckResult.success();
      }
   }
}
