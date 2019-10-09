/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.LayeredVertexConsumerStorage;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.CowEntityModel;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.MooshroomEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.util.math.MatrixStack;

@Environment(value=EnvType.CLIENT)
public class MooshroomMushroomFeatureRenderer<T extends MooshroomEntity>
extends FeatureRenderer<T, CowEntityModel<T>> {
    public MooshroomMushroomFeatureRenderer(FeatureRendererContext<T, CowEntityModel<T>> featureRendererContext) {
        super(featureRendererContext);
    }

    public void method_4195(MatrixStack matrixStack, LayeredVertexConsumerStorage layeredVertexConsumerStorage, int i, T mooshroomEntity, float f, float g, float h, float j, float k, float l, float m) {
        if (((PassiveEntity)mooshroomEntity).isBaby() || ((Entity)mooshroomEntity).isInvisible()) {
            return;
        }
        BlockRenderManager blockRenderManager = MinecraftClient.getInstance().getBlockRenderManager();
        BlockState blockState = ((MooshroomEntity)mooshroomEntity).getMooshroomType().getMushroomState();
        matrixStack.push();
        matrixStack.scale(-1.0f, -1.0f, 1.0f);
        matrixStack.translate(-0.2f, 0.35f, 0.5);
        matrixStack.multiply(Vector3f.POSITIVE_Y.getRotationQuaternion(-42.0f));
        int n = LivingEntityRenderer.method_23622(mooshroomEntity, 0.0f);
        matrixStack.push();
        matrixStack.translate(-0.5, -0.5, 0.5);
        blockRenderManager.renderDynamic(blockState, matrixStack, layeredVertexConsumerStorage, i, n);
        matrixStack.pop();
        matrixStack.push();
        matrixStack.translate(-0.1f, 0.0, -0.6f);
        matrixStack.multiply(Vector3f.POSITIVE_Y.getRotationQuaternion(-42.0f));
        matrixStack.translate(-0.5, -0.5, 0.5);
        blockRenderManager.renderDynamic(blockState, matrixStack, layeredVertexConsumerStorage, i, n);
        matrixStack.pop();
        matrixStack.pop();
        matrixStack.push();
        ((CowEntityModel)this.getModel()).getHead().rotate(matrixStack, 0.0625f);
        matrixStack.scale(-1.0f, -1.0f, 1.0f);
        matrixStack.translate(0.0, 0.7f, -0.2f);
        matrixStack.multiply(Vector3f.POSITIVE_Y.getRotationQuaternion(-12.0f));
        matrixStack.translate(-0.5, -0.5, 0.5);
        blockRenderManager.renderDynamic(blockState, matrixStack, layeredVertexConsumerStorage, i, n);
        matrixStack.pop();
    }
}

