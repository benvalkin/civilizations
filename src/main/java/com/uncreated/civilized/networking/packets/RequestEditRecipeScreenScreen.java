package com.uncreated.civilized.networking.packets;

import static com.uncreated.civilized.CivilizedMod.CIVILIZED_MOD_ID;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.annotation.Nullable;

import org.apache.commons.lang3.function.TriFunction;

import com.uncreated.civilized.core.building.Building;
import com.uncreated.civilized.core.building.ServerBuildingsStore;
import com.uncreated.civilized.core.building.crafting.bills.ProductionBill;
import com.uncreated.civilized.core.building.crafting.bills.ProductionType;
import com.uncreated.civilized.core.building.crafting.bills.strategy.ProductionStrategyType;
import com.uncreated.civilized.core.building.state.ArtisanHouseState;
import com.uncreated.civilized.core.settlement.ClientSettlementsStore;
import com.uncreated.civilized.core.settlement.Settlement;
import com.uncreated.civilized.ui.menu.building.residence.artisan.EditCraftingRecipeMenu;

import net.minecraft.ChatFormatting;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record RequestEditRecipeScreenScreen(UUID buildingId, int containerSize, int productionBillIndex,
      boolean isNewBill) implements CustomPacketPayload {

   public static final Type<RequestEditRecipeScreenScreen> TYPE =
         new Type<>(ResourceLocation.fromNamespaceAndPath(CIVILIZED_MOD_ID, "request_edit_recipe_screen"));

   public static StreamCodec<FriendlyByteBuf, RequestEditRecipeScreenScreen> STREAM_CODEC =
         StreamCodec.ofMember(RequestEditRecipeScreenScreen::encode, RequestEditRecipeScreenScreen::decode);

   public static RequestEditRecipeScreenScreen decode(FriendlyByteBuf buffer) {
      return new RequestEditRecipeScreenScreen(
            buffer.readUUID(),
            buffer.readInt(),
            buffer.readInt(),
            buffer.readBoolean());
   }

   public void encode(FriendlyByteBuf buffer) {
      buffer.writeUUID(buildingId);
      buffer.writeInt(containerSize);
      buffer.writeInt(productionBillIndex);
      buffer.writeBoolean(isNewBill);
   }

   @Override
   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }

   public static void serverReceiveRequestScreen(RequestEditRecipeScreenScreen packet, IPayloadContext context) {

      Optional<Building> building = ServerBuildingsStore.INSTANCE.find(packet.buildingId);
      if (building.isEmpty())
         return;

      if (!(building.get().getState() instanceof ArtisanHouseState artisanHouseState))
         return;

      ProductionBill bill;
      if (packet.isNewBill)
         bill =
               new ProductionBill(
                     "pending_recipe_name",
                     ProductionType.CRAFTING,
                     ProductionStrategyType.PRODUCE_INFINITE,
                     -1,
                     true,
                     List.of(),
                     ItemStack.EMPTY);
      else if (packet.productionBillIndex() < artisanHouseState.getProductionBills().size())
         bill = artisanHouseState.getProductionBills().get(packet.productionBillIndex());
      else
         return;

      Settlement settlement = ClientSettlementsStore.INSTANCE.get(building.get().getSettlementId());

      TriFunction<Integer, Inventory, Player, EditCraftingRecipeMenu> menuSupplier = switch (bill.getProductionType()) {
      case CRAFTING -> (i, inventory, player) -> new EditCraftingRecipeMenu(
            i,
            inventory,
            new SimpleContainer(9),
            settlement,
            building.get(),
            packet.productionBillIndex(),
            packet.isNewBill());
      default -> throw new IllegalArgumentException();
      };

      context.player().openMenu(new MenuProvider() {
         @Override
         public Component getDisplayName() {
            return building.get().getBuildingType().translationDark().withStyle(ChatFormatting.UNDERLINE);
         }

         @Override
         public @Nullable AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
            return menuSupplier.apply(i, inventory, player);
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
