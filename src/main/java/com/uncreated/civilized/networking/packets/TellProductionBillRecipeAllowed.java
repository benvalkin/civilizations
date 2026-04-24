package com.uncreated.civilized.networking.packets;

import static com.uncreated.civilized.CivilizedMod.CIVILIZED_MOD_ID;

import javax.annotation.Nullable;

import com.uncreated.civilized.ui.menu.building.residence.artisan.EditRecipeScreen;

import com.uncreated.civilized.ui.menu.building.residence.artisan.RecipeAllowed;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record TellProductionBillRecipeAllowed(RecipeAllowed recipeAllowed) implements CustomPacketPayload {
   public static final Type<TellProductionBillRecipeAllowed> TYPE =
         new Type<>(ResourceLocation.fromNamespaceAndPath(CIVILIZED_MOD_ID, "tell_production_bill_recipe_allowed"));

   public static StreamCodec<RegistryFriendlyByteBuf, TellProductionBillRecipeAllowed> STREAM_CODEC =
         StreamCodec.ofMember(TellProductionBillRecipeAllowed::encode, TellProductionBillRecipeAllowed::decode);

   public static TellProductionBillRecipeAllowed decode(RegistryFriendlyByteBuf buffer) {
      return new TellProductionBillRecipeAllowed(buffer.readEnum(RecipeAllowed.class));
   }

   public void encode(RegistryFriendlyByteBuf buffer) {
      buffer.writeEnum(recipeAllowed);
   }

   @Override
   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }

   public static void clientReceiveRecipeAllowed(TellProductionBillRecipeAllowed packet, IPayloadContext context) {

      @Nullable
      Screen screen = Minecraft.getInstance().screen;
      if (!(screen instanceof EditRecipeScreen<?> editRecipeScreen))
         return;

      editRecipeScreen.receiveRecipeAllowed(packet.recipeAllowed());
   }

}
