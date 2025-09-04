package com.uncreated.civilized.item;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import com.uncreated.civilized.core.settlement.ClientSettlementsStore;
import com.uncreated.civilized.core.settlement.Settlement;
import org.apache.commons.compress.utils.Lists;

import com.uncreated.civilized.core.building.requirement.EnclosedWallsRequirement;
import com.uncreated.civilized.core.building.requirement.IBuildingRequirement;
import com.uncreated.civilized.core.building.requirement.IBuildingRequirementResult;
import com.uncreated.civilized.core.building.requirement.SpaceRequirement;
import com.uncreated.civilized.core.building.requirement.SurfaceAreaRequirement;
import com.uncreated.civilized.core.building.requirement.blockcount.BlockCountRequirement;
import com.uncreated.civilized.core.building.requirement.registry.BuildingRequirementList;
import com.uncreated.civilized.core.building.requirement.registry.BuildingRequirementRegistry;
import com.uncreated.civilized.client.renderer.BuildingBoundsDragTool;
import com.uncreated.civilized.core.building.Building;
import com.uncreated.civilized.core.building.BuildingType;
import com.uncreated.civilized.core.building.ClientBuildingStore;
import com.uncreated.civilized.ui.menu.building.EstablishBuildingScreen;
import com.uncreated.civilized.ui.style.Colors;

import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

public class BuildingDeedItem extends Item {

   @Getter
   private final BuildingType buildingType;

   public BuildingDeedItem(Properties properties, BuildingType buildingType) {
      super(properties);
      this.buildingType = buildingType;
   }

   @Override
   public InteractionResult use(Level level, Player player, InteractionHand hand) {

      if (!level.isClientSide)
         return InteractionResult.PASS;

      // HitResult hitResult = ProjectileUtil.getHitResultOnViewVector(player, e -> true,
      // player.blockInteractionRange());
      HitResult hitResult = Minecraft.getInstance().hitResult;
      if (hitResult instanceof BlockHitResult blockHitResult && blockHitResult.getType() == HitResult.Type.MISS) {
         BuildingBoundsDragTool.stopDragging();
      }

      return super.use(level, player, hand);
   }

   public InteractionResult useOn(UseOnContext context) {

      BlockPos clickedBlockPos = context.getClickedPos();
      BlockPos clickedAir = clickedBlockPos.mutable().move(context.getClickedFace());
      Player player = context.getPlayer();

      if (!context.getLevel().isClientSide || player == null)
         return InteractionResult.PASS;

      if (BuildingBoundsDragTool.isBusyDragging(player)) {
         BuildingBoundsDragTool.completeDragging(player, clickedAir);
      } else {
         BuildingBoundsDragTool.startDraggingBounds(player, clickedAir);
      }

      if (BuildingBoundsDragTool.isDraggingComplete(player)) {

         BuildingBoundsDragTool.BuildingBoundsDragResult boundsResult =
               BuildingBoundsDragTool.getBuildingBoundsDragResult(context.getLevel());
         BuildingBoundsDragTool.stopDragging();

         Optional<Building> overlappingOther =
               ClientBuildingStore.INSTANCE.findOverlappingBuilding(boundsResult.bounds());
         if (overlappingOther.isPresent()) {
            player.displayClientMessage(
                  Component
                        .translatable(
                              "message.building.placement.validation.building_overlapping",
                              buildingType.translation(),
                              overlappingOther.get().getBuildingType().translation())
                        .withColor(Colors.VALIDATION_ERROR),
                  false);
            return InteractionResult.FAIL;
         }

         if (!buildingType.isWorksite()) {
            if (!boundsResult.centerIsAir()) {
               player.displayClientMessage(
                     Component
                           .translatable(
                                 "message.building.placement.validation.center_obstructed",
                                 buildingType.translation(),
                                 boundsResult.bounds().getCenter().toShortString())
                           .withColor(Colors.VALIDATION_ERROR),
                     false);
               return InteractionResult.FAIL;
            }

            if (!boundsResult.centerIsInside()) {
               player.displayClientMessage(
                     Component
                           .translatable(
                                 "message.building.placement.validation.center_no_roof",
                                 buildingType.translation(),
                                 boundsResult.bounds().getCenter().toShortString())
                           .withColor(Colors.VALIDATION_ERROR),
                     false);
               return InteractionResult.FAIL;
            }
         }

         int buildingLevel = 1;
         BuildingRequirementList requirements =
               BuildingRequirementRegistry.getBuildingRequirements(buildingType, buildingLevel);
         List<IBuildingRequirementResult> requirementResults = Lists.newArrayList();

         Set<BlockPos> validFloorBlocks = Set.of();
         for (IBuildingRequirement requirement : requirements) {

            if (requirement instanceof SpaceRequirement s) {
               SpaceRequirement.Result spaceResult = s.getResult(context.getLevel(), boundsResult.bounds());
               validFloorBlocks = spaceResult.getValidFloorBlocks();
               requirementResults.add(spaceResult);
               // BAD IMPLEMENTATION: dependant requirements mean that they are also dependent on the order they are
               // defined in.
               // EnclosedWallsRequirements will not work if it comes before SpaceRequirement in the list.
            }
            if (requirement instanceof EnclosedWallsRequirement ew)
               requirementResults.add(ew.getResult(context.getLevel(), boundsResult.bounds(), validFloorBlocks));
            if (requirement instanceof BlockCountRequirement bt)
               requirementResults.add(bt.getResult(context.getLevel(), boundsResult.bounds()));
            if (requirement instanceof SurfaceAreaRequirement sa)
               requirementResults.add(sa.getResult(boundsResult.bounds()));
         }

         Minecraft.getInstance()
               .setScreen(new EstablishBuildingScreen(buildingType, boundsResult.bounds(), requirementResults));

         return InteractionResult.SUCCESS;
      }

      return InteractionResult.SUCCESS;
   }
}
