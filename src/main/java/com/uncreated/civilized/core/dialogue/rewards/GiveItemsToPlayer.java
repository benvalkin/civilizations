package com.uncreated.civilized.core.dialogue.rewards;

import static com.uncreated.civilized.CivilizedMod.CIVILIZED_MOD_ID;

import java.util.List;

import lombok.Getter;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

@Getter
public class GiveItemsToPlayer implements CustomPacketPayload {

   private final List<ItemStack> itemStacks;

   public GiveItemsToPlayer(List<ItemStack> itemStacks) {
      this.itemStacks = itemStacks;
   }

   public static final StreamCodec<RegistryFriendlyByteBuf, GiveItemsToPlayer> STREAM_CODEC =
         StreamCodec.composite(
               ItemStack.OPTIONAL_LIST_STREAM_CODEC,
               GiveItemsToPlayer::getItemStacks,
               GiveItemsToPlayer::new);

   @Override
   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }

   public static final CustomPacketPayload.Type<GiveItemsToPlayer> TYPE =
         new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(CIVILIZED_MOD_ID, "give_item_to_player"));
}
