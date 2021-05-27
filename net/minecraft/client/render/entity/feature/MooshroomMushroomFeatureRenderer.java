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
import net.minecraft.util.math.Vec3f;

@Environment(value=EnvType.CLIENT)
public class MooshroomMushroomFeatureRenderer<T extends MooshroomEntity>
extends FeatureRenderer<T, CowEntityModel<T>> {
    public MooshroomMushroomFeatureRenderer(FeatureRendererContext<T, CowEntityModel<T>> featureRendererContext) {
        super(featureRendererContext);
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
        BlockRenderManager blockRenderManager = minecraftClient.getBlockRenderManager();
        BlockState blockState = ((MooshroomEntity)mooshroomEntity).getMooshroomType().getMushroomState();
        int m = LivingEntityRenderer.getOverlay(mooshroomEntity, 0.0f);
        BakedModel bakedModel = blockRenderManager.getModel(blockState);
        matrixStack.push();
        matrixStack.translate(0.2f, -0.35f, 0.5);
        matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(-48.0f));
        matrixStack.scale(-1.0f, -1.0f, 1.0f);
        matrixStack.translate(-0.5, -0.5, -0.5);
        this.method_37314(matrixStack, vertexConsumerProvider, i, bl, blockRenderManager, blockState, m, bakedModel);
        matrixStack.pop();
        matrixStack.push();
        matrixStack.translate(0.2f, -0.35f, 0.5);
        matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(42.0f));
        matrixStack.translate(0.1f, 0.0, -0.6f);
        matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(-48.0f));
        matrixStack.scale(-1.0f, -1.0f, 1.0f);
        matrixStack.translate(-0.5, -0.5, -0.5);
        this.method_37314(matrixStack, vertexConsumerProvider, i, bl, blockRenderManager, blockState, m, bakedModel);
        matrixStack.pop();
        matrixStack.push();
        ((CowEntityModel)this.getContextModel()).getHead().rotate(matrixStack);
        matrixStack.translate(0.0, -0.7f, -0.2f);
        matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(-78.0f));
        matrixStack.scale(-1.0f, -1.0f, 1.0f);
        matrixStack.translate(-0.5, -0.5, -0.5);
        this.method_37314(matrixStack, vertexConsumerProvider, i, bl, blockRenderManager, blockState, m, bakedModel);
        matrixStack.pop();
    }

    private void method_37314(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, boolean bl, BlockRenderManager blockRenderManager, BlockState blockState, int j, BakedModel bakedModel) {
        if (bl) {
            blockRenderManager.getModelRenderer().render(matrixStack.peek(), vertexConsumerProvider.getBuffer(RenderLayer.getOutline(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE)), blockState, bakedModel, 0.0f, 0.0f, 0.0f, i, j);
        } else {
            blockRenderManager.renderBlockAsEntity(blockState, matrixStack, vertexConsumerProvider, i, j);
        }
    }
}

