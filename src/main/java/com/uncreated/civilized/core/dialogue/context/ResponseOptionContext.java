package com.uncreated.civilized.core.dialogue.context;

import com.uncreated.civilized.core.dialogue.ResponseOption;

import lombok.Getter;

@Getter
public class ResponseOptionContext {

   private final ResponseOption selectedOption;
   private final DialogueContext dialogueContext;

   public ResponseOptionContext(ResponseOption selectedOption, DialogueContext dialogueContext) {
      this.selectedOption = selectedOption;
      this.dialogueContext = dialogueContext;
   }
}
