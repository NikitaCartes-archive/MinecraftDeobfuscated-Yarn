/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.LayeredVertexConsumerStorage;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.MatrixStack;

@Environment(value=EnvType.CLIENT)
public class Deadmau5FeatureRenderer
extends FeatureRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> {
    public Deadmau5FeatureRenderer(FeatureRendererContext<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> featureRendererContext) {
        super(featureRendererContext);
    }

    public void method_4181(MatrixStack matrixStack, LayeredVertexConsumerStorage layeredVertexConsumerStorage, int i, AbstractClientPlayerEntity abstractClientPlayerEntity, float f, float g, float h, float j, float k, float l, float m) {
        if (!"deadmau5".equals(abstractClientPlayerEntity.getName().getString()) || !abstractClientPlayerEntity.hasSkinTexture() || abstractClientPlayerEntity.isInvisible()) {
            return;
        }
        VertexConsumer vertexConsumer = layeredVertexConsumerStorage.getBuffer(RenderLayer.method_23017(abstractClientPlayerEntity.getSkinTexture()));
        LivingEntityRenderer.method_23184(abstractClientPlayerEntity, vertexConsumer, 0.0f);
        for (int n = 0; n < 2; ++n) {
            float o = MathHelper.lerp(h, abstractClientPlayerEntity.prevYaw, abstractClientPlayerEntity.yaw) - MathHelper.lerp(h, abstractClientPlayerEntity.prevBodyYaw, abstractClientPlayerEntity.bodyYaw);
            float p = MathHelper.lerp(h, abstractClientPlayerEntity.prevPitch, abstractClientPlayerEntity.pitch);
            matrixStack.push();
            matrixStack.multiply(Vector3f.POSITIVE_Y.getRotationQuaternion(o, true));
            matrixStack.multiply(Vector3f.POSITIVE_X.getRotationQuaternion(p, true));
            matrixStack.translate(0.375f * (float)(n * 2 - 1), 0.0, 0.0);
            matrixStack.translate(0.0, -0.375, 0.0);
            matrixStack.multiply(Vector3f.POSITIVE_X.getRotationQuaternion(-p, true));
            matrixStack.multiply(Vector3f.POSITIVE_Y.getRotationQuaternion(-o, true));
            float q = 1.3333334f;
            matrixStack.scale(1.3333334f, 1.3333334f, 1.3333334f);
            ((PlayerEntityModel)this.getModel()).renderEars(matrixStack, vertexConsumer, 0.0625f, i);
            matrixStack.pop();
        }
        vertexConsumer.clearDefaultOverlay();
    }
}

