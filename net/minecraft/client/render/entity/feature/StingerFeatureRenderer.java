/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.LayeredVertexConsumerStorage;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.StickingOutThingsFeatureRenderer;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class StingerFeatureRenderer<T extends LivingEntity, M extends PlayerEntityModel<T>>
extends StickingOutThingsFeatureRenderer<T, M> {
    private static final Identifier field_20529 = new Identifier("textures/entity/bee/bee_stinger.png");

    public StingerFeatureRenderer(LivingEntityRenderer<T, M> livingEntityRenderer) {
        super(livingEntityRenderer);
    }

    @Override
    protected int getThingCount(T livingEntity) {
        return ((LivingEntity)livingEntity).getStingerCount();
    }

    @Override
    protected void renderThing(MatrixStack matrixStack, LayeredVertexConsumerStorage layeredVertexConsumerStorage, Entity entity, float f, float g, float h, float i) {
        float j = MathHelper.sqrt(f * f + h * h);
        float k = (float)(Math.atan2(f, h) * 57.2957763671875);
        float l = (float)(Math.atan2(g, j) * 57.2957763671875);
        matrixStack.translate(0.0, 0.0, 0.0);
        matrixStack.multiply(Vector3f.POSITIVE_Y.getRotationQuaternion(k - 90.0f));
        matrixStack.multiply(Vector3f.POSITIVE_Z.getRotationQuaternion(l));
        float m = 0.0f;
        float n = 0.125f;
        float o = 0.0f;
        float p = 0.0625f;
        float q = 0.03125f;
        matrixStack.multiply(Vector3f.POSITIVE_X.getRotationQuaternion(45.0f));
        matrixStack.scale(0.03125f, 0.03125f, 0.03125f);
        matrixStack.translate(2.5, 0.0, 0.0);
        int r = entity.getLightmapCoordinates();
        VertexConsumer vertexConsumer = layeredVertexConsumerStorage.getBuffer(RenderLayer.getEntityCutoutNoCull(field_20529));
        for (int s = 0; s < 4; ++s) {
            matrixStack.multiply(Vector3f.POSITIVE_X.getRotationQuaternion(90.0f));
            Matrix4f matrix4f = matrixStack.peek();
            StingerFeatureRenderer.method_23295(vertexConsumer, matrix4f, -4.5f, -1, 0.0f, 0.0f, r);
            StingerFeatureRenderer.method_23295(vertexConsumer, matrix4f, 4.5f, -1, 0.125f, 0.0f, r);
            StingerFeatureRenderer.method_23295(vertexConsumer, matrix4f, 4.5f, 1, 0.125f, 0.0625f, r);
            StingerFeatureRenderer.method_23295(vertexConsumer, matrix4f, -4.5f, 1, 0.0f, 0.0625f, r);
        }
    }

    private static void method_23295(VertexConsumer vertexConsumer, Matrix4f matrix4f, float f, int i, float g, float h, int j) {
        vertexConsumer.vertex(matrix4f, f, i, 0.0f).color(255, 255, 255, 255).texture(g, h).defaultOverlay(OverlayTexture.DEFAULT_UV).light(j).normal(0.0f, 1.0f, 0.0f).next();
    }
}

