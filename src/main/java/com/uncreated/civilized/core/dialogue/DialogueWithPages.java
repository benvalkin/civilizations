package com.uncreated.civilized.core.dialogue;

import javax.annotation.Nullable;

import lombok.Getter;

public class DialogueWithPages {

   @Getter
   private int pageCount;
   private @Nullable Dialogue firstPage;
   private @Nullable Dialogue currentPage;

   protected DialogueWithPages() {
      this.pageCount = 0;
   }

   public DialogueWithPages page(Dialogue page) {
      if (firstPage == null)
         firstPage = page;

      if (currentPage != null)
         currentPage.nextPage(page);

      currentPage = page;

      pageCount++;
      return this;
   }

   public int pageCount() {
      return pageCount;
   }

   public static DialogueWithPages dialogueWithpages() {
      return new DialogueWithPages();
   }

   public Dialogue create() {
      if (firstPage == null)
         throw new IllegalStateException("DialogueWithPages must be given at least one page_");

      return firstPage;
   }
}
