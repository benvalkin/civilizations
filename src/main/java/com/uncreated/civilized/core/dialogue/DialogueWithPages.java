package com.uncreated.civilized.core.dialogue;

import java.util.LinkedList;
import java.util.List;

import lombok.Builder;
import lombok.Getter;
import net.minecraft.network.chat.Component;

@Builder
public class DialogueWithPages implements IVillageDialogue {

   @Getter
   private final String key;

   private List<Dialogue> pages;
   @Getter
   private int pageIndex;
   @Getter
   private Dialogue currentPage;

   @Override
   public Component getVillagerSpeech() {
      return currentPage.getVillagerSpeech();
   }

   @Override
   public List<ResponseOption> getResponseOptions() {
      return currentPage.getResponseOptions();
   }

   public Dialogue goNextPage() {
      if (!hasNextPage())
         return currentPage;

      pageIndex++;
      currentPage = pages.get(pageIndex);
      return currentPage;
   }

   public boolean hasNextPage() {
      return pageIndex < pages.size() - 1;
   }

   public int pageCount() {
      return pages.size();
   }

   public static DialogueWithPagesBuilder create(String key) {
      return new DialogueWithPagesBuilder().key(key).pages(new LinkedList<>());
   }

   public static class DialogueWithPagesBuilder {
      public DialogueWithPagesBuilder page(Dialogue page) {
         pages.add(page);
         return this;
      }

      public DialogueWithPages build() {

         if (pages.isEmpty())
            throw new IllegalArgumentException("Dialogue with pages needs at least 1 page.");

         pageIndex = 0;
         currentPage = pages.getFirst();
         return new DialogueWithPages(key, pages, pageIndex, currentPage);
      }
   }
}
