package com.uncreated.civilized.core.dialogue;

import javax.annotation.Nullable;

import com.uncreated.civilized.core.dialogue.context.ResponseOptionContext;

import lombok.Getter;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;

@Getter
public class ResponseOption {
   private String key;
   private Component playerSpeech;
   private IResponseOptionVisibleCheck visibleCheck;
   private IResponseOptionEnabledCheck enabledCheck;
   private IOnResponseSelectedAction onPress;

   private ResponseOption(String key, Component playerSpeech) {
      this.key = key;
      this.playerSpeech = playerSpeech;
      this.visibleCheck = IResponseOptionVisibleCheck.alwaysVisible();
      this.enabledCheck = IResponseOptionEnabledCheck.alwaysEnabled();
   }

   public ResponseOption onSelect(IOnResponseSelectedAction onSelect) {
      this.onPress = onSelect;
      return this;
   }

   public ResponseOption shouldBeVisible(IResponseOptionVisibleCheck visibleCheck) {
      this.visibleCheck = visibleCheck;
      return this;
   }

   public ResponseOption shouldBeEnabled(IResponseOptionEnabledCheck enabledCheck) {
      this.enabledCheck = enabledCheck;
      return this;
   }

   public ResponseOption onSelectDoNothing() {
      this.onPress = context -> DialogueAction.DO_NOTHING;
      return this;
   }

   public ResponseOption onSelectCloseDialogue() {
      this.onPress = context -> DialogueAction.CLOSE_DIALOGUE;
      return this;
   }

   public ResponseOption onSelectGoNextPage() {
      this.onPress = context -> DialogueAction.NEXT_PAGE;
      return this;
   }

   public static ResponseOption create(String key, Component playerSpeech) {
      return new ResponseOption(key, playerSpeech).onSelectDoNothing();
   }

   public enum DialogueAction {
      DO_NOTHING, NEXT_PAGE, CLOSE_DIALOGUE
   }

   public static ResponseOption nextPage() {
      return ResponseOption.create("next_page", Component.translatable("villager.dialogue.response.next_page"))
            .onSelectGoNextPage();
   }

   public static ResponseOption nextPage(Component playerSpeech) {
      return ResponseOption.create("next_page", playerSpeech).onSelectGoNextPage();
   }

   public interface IOnResponseSelectedAction {
      DialogueAction onOptionSelected(ResponseOptionContext context);
   }

   public interface IResponseOptionEnabledCheck {
      EnabledCheckResult isOptionEnabled(ResponseOptionContext context);

      static IResponseOptionEnabledCheck alwaysEnabled() {
         return context -> new EnabledCheckResult(true, null);
      }
   }

   public interface IResponseOptionVisibleCheck {
      boolean isOptionVisible(ResponseOptionContext context);

      static IResponseOptionVisibleCheck alwaysVisible() {
         return context -> true;
      }
   }

   @Getter
   public static class EnabledCheckResult {
      private final boolean isEnabled;
      private final @Nullable Tooltip tooltip;

      private EnabledCheckResult(boolean isEnabled, @Nullable Tooltip tooltip) {
         this.isEnabled = isEnabled;
         this.tooltip = tooltip;
      }

      public static EnabledCheckResult success() {
         return new EnabledCheckResult(true, null);
      }
      public static EnabledCheckResult failed(Tooltip tooltip) {
         return new EnabledCheckResult(false, tooltip);
      }
      public static EnabledCheckResult failed() {
         return new EnabledCheckResult(false, null);
      }
   }
}
