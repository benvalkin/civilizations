package com.uncreated.civilized.core.dialogue.specialized;

import com.uncreated.civilized.core.dialogue.Dialogue;
import com.uncreated.civilized.core.dialogue.context.DialogueContext;

import lombok.Getter;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

@Getter
public class ItemDepotDialogue extends Dialogue {

   private ICanConsumeItemCheck canConsumeItemCheck;
   private IConsumeItemAction consumeItemAction;

   public ItemDepotDialogue() {
      super(Component.empty());
      canConsumeItemCheck = (context, itemStack, hand) -> true;
      consumeItemAction = (context, itemStack, hand) -> itemStack.consumeAndReturn(1, context.getPlayer());
   }

   public ItemDepotDialogue canConsumeItem(ICanConsumeItemCheck canConsumeItemCheck) {
      this.canConsumeItemCheck = canConsumeItemCheck;
      return this;
   }

   public ItemDepotDialogue consumeItemAction(IConsumeItemAction consumeItemAction) {
      this.consumeItemAction = consumeItemAction;
      return this;
   }

   public interface ICanConsumeItemCheck {
      boolean canConsumeItem(DialogueContext context, ItemStack itemStack, InteractionHand hand);
   }

   public interface IConsumeItemAction {
      ItemStack consumeItem(DialogueContext context, ItemStack itemStack, InteractionHand hand);
   }

   public static ItemDepotDialogue itemDepotDialogue() {
      return new ItemDepotDialogue();
   }
}
