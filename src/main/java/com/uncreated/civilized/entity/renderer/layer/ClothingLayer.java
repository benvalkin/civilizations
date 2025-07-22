package com.uncreated.civilized.entity.renderer.layer;

import static com.uncreated.civilized.CivilizedMod.CIVILIZED_MOD_ID;

import com.mojang.blaze3d.vertex.PoseStack;
import com.uncreated.civilized.core.villagerinfo.VillagerOccupation;
import com.uncreated.civilized.entity.renderer.CivilizedVillagerRenderState;
import com.uncreated.civilized.entity.renderer.CivilizedVillagerRenderer;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;

// The generic parameters need the proper types you used everywhere else up to this point.
public class ClothingLayer
      extends RenderLayer<CivilizedVillagerRenderState, HumanoidModel<CivilizedVillagerRenderState>> {
   private ResourceLocation clothesTexture;

   // private final CivilizedVillagerModel model;

   // Create the render layer. The renderer parameter is required for passing to super.
   // Other parameters can be added as needed. For example, we need the EntityModelSet for model baking.
   public ClothingLayer(CivilizedVillagerRenderer renderer, EntityModelSet entityModelSet) {
      super(renderer);
      // Bake and store our layer definition, using the ModelLayerLocation from back when we registered the layer
      // definition.
      // If applicable, you can also store multiple models this way and use them below.
      // this.model = new CivilizedVillagerModel(entityModelSet.bakeLayer(MY_LAYER));
      entityModelSet.bakeLayer(ModelLayers.PLAYER);
   }

   ResourceLocation TEXTURE_CLOTHES_FARMER_1 =
         ResourceLocation.fromNamespaceAndPath(CIVILIZED_MOD_ID, "textures/entity/civilized_villager_2.png");

   ResourceLocation TEXTURE_CLOTHES_WOODCUTTER_1 =
         ResourceLocation.fromNamespaceAndPath(CIVILIZED_MOD_ID, "textures/entity/civilized_villager_3.png");

   @Override
   public void render(
         PoseStack poseStack,
         MultiBufferSource bufferSource,
         int packedLight,
         CivilizedVillagerRenderState renderState,
         float yRot,
         float xRot) {

      if (renderState.occupation == VillagerOccupation.WOODCUTTER)
         clothesTexture = TEXTURE_CLOTHES_WOODCUTTER_1;
      else if (renderState.occupation == VillagerOccupation.FARMER)
         clothesTexture = TEXTURE_CLOTHES_FARMER_1;
      else
         clothesTexture = TEXTURE_CLOTHES_FARMER_1;

      RenderLayer.renderColoredCutoutModel(
            getParentModel(),
            clothesTexture,
            poseStack,
            bufferSource,
            packedLight,
            renderState,
            0xffffff);
   }
}
