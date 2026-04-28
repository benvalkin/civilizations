package com.uncreated.civilized.networking.packets;

import static com.uncreated.civilized.CivilizedMod.CIVILIZED_MOD_ID;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.annotation.Nullable;

import com.uncreated.civilized.core.building.Building;
import com.uncreated.civilized.core.building.ServerBuildingsStore;
import com.uncreated.civilized.core.building.production.bills.ProductionBill;
import com.uncreated.civilized.core.building.production.bills.ProductionType;
import com.uncreated.civilized.core.building.production.bills.ProductionTypes;
import com.uncreated.civilized.core.building.state.artisan.ArtisanHouseState;
import com.uncreated.civilized.core.settlement.ClientSettlementsStore;
import com.uncreated.civilized.core.settlement.Settlement;

import net.minecraft.ChatFormatting;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record RequestEditRecipeProductionScreen(UUID buildingId, int containerSize, ProductionType productionType,
      int productionBillIndex, boolean isNewBill) implements CustomPacketPayload {

   public static final Type<RequestEditRecipeProductionScreen> TYPE =
         new Type<>(ResourceLocation.fromNamespaceAndPath(CIVILIZED_MOD_ID, "request_edit_recipe_screen"));

   public static StreamCodec<FriendlyByteBuf, RequestEditRecipeProductionScreen> STREAM_CODEC =
         StreamCodec.ofMember(RequestEditRecipeProductionScreen::encode, RequestEditRecipeProductionScreen::decode);

   public static RequestEditRecipeProductionScreen decode(FriendlyByteBuf buffer) {
      return new RequestEditRecipeProductionScreen(
            buffer.readUUID(),
            buffer.readInt(),
            ProductionTypes.getFromResourceLocation(buffer.readResourceLocation()),
            buffer.readInt(),
            buffer.readBoolean());
   }

   public void encode(FriendlyByteBuf buffer) {
      buffer.writeUUID(buildingId);
      buffer.writeInt(containerSize);
      buffer.writeResourceLocation(productionType.resourceLocation());
      buffer.writeInt(productionBillIndex);
      buffer.writeBoolean(isNewBill);
   }

   @Override
   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }

   public static void serverReceiveRequestScreen(RequestEditRecipeProductionScreen packet, IPayloadContext context) {

      Optional<Building> building = ServerBuildingsStore.INSTANCE.find(packet.buildingId);
      if (building.isEmpty())
         return;

      if (!(building.get().getState() instanceof ArtisanHouseState artisanHouseState))
         return;

      Optional<List<ProductionBill>> existingBills = artisanHouseState.tryGetProductionBills(packet.productionType);
      if (existingBills.isEmpty())
         return;

      if (!packet.isNewBill() && packet.productionBillIndex() >= existingBills.get().size())
         return;

      Settlement settlement = ClientSettlementsStore.INSTANCE.get(building.get().getSettlementId());

      context.player().openMenu(new MenuProvider() {
         @Override
         public Component getDisplayName() {
            return building.get().getBuildingType().translationDark().withStyle(ChatFormatting.UNDERLINE);
         }

         @Override
         public @Nullable AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
            return packet.productionType.menuSupplier()
                  .createMenu(
                        i,
                        inventory,
                        settlement,
                        building.get(),
                        packet.productionBillIndex(),
                        packet.isNewBill());
         }

         @Override
         public void writeClientSideData(AbstractContainerMenu menu, RegistryFriendlyByteBuf buffer) {
            buffer.writeInt(packet.containerSize());
            buffer.writeUUID(settlement.getSettlementId());
            buffer.writeUUID(building.get().getBuildingId());
            buffer.writeInt(packet.productionBillIndex());
            buffer.writeBoolean(packet.isNewBill());
         }
      });
   }
}
