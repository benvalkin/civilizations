package com.uncreated.civilized.core.building.production;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Queue;

import com.mojang.datafixers.util.Pair;
import com.uncreated.civilized.core.building.production.bills.ProductionBill;
import com.uncreated.civilized.core.building.production.bills.ProductionType;
import com.uncreated.civilized.core.building.production.bills.strategy.ProductionStrategyType;
import com.uncreated.civilized.core.building.production.orders.ProductionOrder;

import lombok.Getter;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;

public abstract class RecipeProductionMachine<Order extends ProductionOrder> {

   private final Queue<Order> orders;

   public RecipeProductionMachine() {
      orders = new LinkedList<>();
   }

   public void registerOrder(Order productionOrder) {
      orders.add(productionOrder);
   }

   public Collection<Order> getOrders() {
      return orders;
   }

   public abstract Order createOrderFromBill(String key, ProductionBill bill, ServerLevel serverLevel);

   public abstract ProductionType getProductionType();

   @Getter
   private int productionTokens = 0;

   public void consumeToken() {
      productionTokens--;

      if (productionTokens <= 0)
         productionTokens = 0;
   }

   public void consumeTokens(int numberOfTokens) {
      productionTokens -= numberOfTokens;

      if (productionTokens <= 0)
         productionTokens = 0;
   }

   public Optional<Pair<ProductionOrder, PendingProductionOutput>> tryGetNextOrder(
         List<Container> ingredientsChests,
         List<Container> stockChests) {

      int attempts = 0;
      do {
         Order currentlyProcessing = orders.peek();

         if (productionTokens > 0) {
            Optional<PendingProductionOutput> possibleOutput =
                  checkProductionPossible(currentlyProcessing, ingredientsChests, stockChests);
            if (possibleOutput.isPresent())
               return Optional.of(Pair.of(currentlyProcessing, possibleOutput.get()));
         }

         // if it is not possible to produce the current order, try move onto the next one
         orders.add(orders.remove()); // reset and move to back of the list
         productionTokens = currentlyProcessing.getBill().getStartingProductionTokens();
         attempts++;
      } while (attempts < orders.size());

      return Optional.empty();
   }

   private Optional<PendingProductionOutput> checkProductionPossible(
         ProductionOrder order,
         List<Container> ingredientsChests,
         List<Container> stockChests) {

      if (!order.getBill().isEnabled())
         return Optional.empty();

      PendingProductionOutput pendingOutput = order.getNextOutput(ingredientsChests, stockChests);
      if (!pendingOutput.canProduce())
         return Optional.empty();

      if (!(order.getBill().getProductionStrategy().getType() == ProductionStrategyType.PRODUCE_INFINITE
            || pendingOutput.stockDeficit() > 0))
         return Optional.empty();

      return Optional.of(pendingOutput);
   }
}
