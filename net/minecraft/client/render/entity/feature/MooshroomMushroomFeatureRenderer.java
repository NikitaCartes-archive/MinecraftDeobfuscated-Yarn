/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.CowEntityModel;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.MooshroomEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.util.math.RotationAxis;

@Environment(value=EnvType.CLIENT)
public class MooshroomMushroomFeatureRenderer<T extends MooshroomEntity>
extends FeatureRenderer<T, CowEntityModel<T>> {
    private final BlockRenderManager blockRenderManager;

    public MooshroomMushroomFeatureRenderer(FeatureRendererContext<T, CowEntityModel<T>> context, BlockRenderManager blockRenderManager) {
        super(context);
        this.blockRenderManager = blockRenderManager;
    }

    @Override
    public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, T mooshroomEntity, float f, float g, float h, float j, float k, float l) {
        boolean bl;
        if (((PassiveEntity)mooshroomEntity).isBaby()) {
            return;
        }
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        boolean bl2 = bl = minecraftClient.hasOutline((Entity)mooshroomEntity) && ((Entity)mooshroomEntity).isInvisible();
        if (((Entity)mooshroomEntity).isInvisible() && !bl) {
            return;
        }
        BlockState blockState = ((MooshroomEntity)mooshroomEntity).getVariant().getMushroomState();
        int m = LivingEntityRenderer.getOverlay(mooshroomEntity, 0.0f);
        BakedModel bakedModel = this.blockRenderManager.getModel(blockState);
        matrixStack.push();
        matrixStack.translate(0.2f, -0.35f, 0.5f);
        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-48.0f));
        matrixStack.scale(-1.0f, -1.0f, 1.0f);
        matrixStack.translate(-0.5f, -0.5f, -0.5f);
        this.renderMushroom(matrixStack, vertexConsumerProvider, i, bl, blockState, m, bakedModel);
        matrixStack.pop();
        matrixStack.push();
        matrixStack.translate(0.2f, -0.35f, 0.5f);
        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(42.0f));
        matrixStack.translate(0.1f, 0.0f, -0.6f);
        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-48.0f));
        matrixStack.scale(-1.0f, -1.0f, 1.0f);
        matrixStack.translate(-0.5f, -0.5f, -0.5f);
        this.renderMushroom(matrixStack, vertexConsumerProvider, i, bl, blockState, m, bakedModel);
        matrixStack.pop();
        matrixStack.push();
        ((CowEntityModel)this.getContextModel()).getHead().rotate(matrixStack);
        matrixStack.translate(0.0f, -0.7f, -0.2f);
        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-78.0f));
        matrixStack.scale(-1.0f, -1.0f, 1.0f);
        matrixStack.translate(-0.5f, -0.5f, -0.5f);
        this.renderMushroom(matrixStack, vertexConsumerProvider, i, bl, blockState, m, bakedModel);
        matrixStack.pop();
    }

    private void renderMushroom(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, boolean renderAsModel, BlockState mushroomState, int overlay, BakedModel mushroomModel) {
        if (renderAsModel) {
            this.blockRenderManager.getModelRenderer().render(matrices.peek(), vertexConsumers.getBuffer(RenderLayer.getOutline(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE)), mushroomState, mushroomModel, 0.0f, 0.0f, 0.0f, light, overlay);
        } else {
            this.blockRenderManager.renderBlockAsEntity(mushroomState, matrices, vertexConsumers, light, overlay);
        }
    }
}

