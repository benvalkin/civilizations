package com.uncreated.civilized.ui.components.multiline;

import java.util.OptionalInt;

import net.minecraft.Util;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractStringWidget;
import net.minecraft.network.chat.Component;
import net.minecraft.util.SingleKeyCache;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class ImprovedMultiLineTextWidget extends AbstractStringWidget {
   private OptionalInt maxWidth;
   private OptionalInt maxRows;
   private final SingleKeyCache<CacheKey, ImprovedMultilineLabel> cache;
   private boolean centered;
   private boolean dropShadows;

   public ImprovedMultiLineTextWidget(Component message, Font font) {
      this(0, 0, message, font);
   }

   public ImprovedMultiLineTextWidget(int x, int y, Component message, Font font) {
      super(x, y, 0, 0, message, font);
      this.maxWidth = OptionalInt.empty();
      this.maxRows = OptionalInt.empty();
      this.centered = false;
      this.cache =
            Util.singleKeyCache(
                  (key) -> key.maxRows.isPresent()
                        ? ImprovedMultilineLabel
                              .create(font, key.maxWidth, key.maxRows.getAsInt(), dropShadows, key.message)
                        : ImprovedMultilineLabel.create(font, key.message, key.maxWidth, dropShadows));
      this.dropShadows = true;
      this.active = false;
   }

   public ImprovedMultiLineTextWidget dropShadows(boolean enabled) {
      this.dropShadows = enabled;
      return this;
   }

   public ImprovedMultiLineTextWidget setColor(int p_270378_) {
      super.setColor(p_270378_);
      return this;
   }

   public ImprovedMultiLineTextWidget setMaxWidth(int maxWidth) {
      this.maxWidth = OptionalInt.of(maxWidth);
      return this;
   }

   public ImprovedMultiLineTextWidget setMaxRows(int maxRows) {
      this.maxRows = OptionalInt.of(maxRows);
      return this;
   }

   public ImprovedMultiLineTextWidget setCentered(boolean centered) {
      this.centered = centered;
      return this;
   }

   public int getWidth() {
      return this.cache.getValue(this.getFreshCacheKey()).getWidth();
   }

   public int getHeight() {
      return this.cache.getValue(this.getFreshCacheKey()).getLineCount() * 9;
   }

   public void renderWidget(GuiGraphics p_282535_, int p_261774_, int p_261640_, float p_261514_) {
      ImprovedMultilineLabel multilinelabel = this.cache.getValue(this.getFreshCacheKey());
      int i = this.getX();
      int j = this.getY();
      int k = 9;
      int l = this.getColor();
      if (this.centered) {
         multilinelabel.renderCentered(p_282535_, i + this.getWidth() / 2, j, k, l);
      } else {
         multilinelabel.renderLeftAligned(p_282535_, i, j, k, l);
      }

   }

   private CacheKey getFreshCacheKey() {
      return new CacheKey(this.getMessage(), this.maxWidth.orElse(Integer.MAX_VALUE), this.maxRows);
   }

   @OnlyIn(Dist.CLIENT)
   static record CacheKey(Component message, int maxWidth, OptionalInt maxRows) {
   }
}
