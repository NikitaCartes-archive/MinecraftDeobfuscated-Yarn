/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.StuckObjectsFeatureRenderer;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.Matrix3f;
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class StuckStingersFeatureRenderer<T extends LivingEntity, M extends PlayerEntityModel<T>>
extends StuckObjectsFeatureRenderer<T, M> {
    private static final Identifier field_20529 = new Identifier("textures/entity/bee/bee_stinger.png");

    public StuckStingersFeatureRenderer(LivingEntityRenderer<T, M> livingEntityRenderer) {
        super(livingEntityRenderer);
    }

    @Override
    protected int getObjectCount(T entity) {
        return ((LivingEntity)entity).getStingerCount();
    }

    @Override
    protected void renderObject(MatrixStack matrix, VertexConsumerProvider vertexConsumers, int i, Entity entity, float tickDelta, float f, float g, float h) {
        float j = MathHelper.sqrt(tickDelta * tickDelta + g * g);
        float k = (float)(Math.atan2(tickDelta, g) * 57.2957763671875);
        float l = (float)(Math.atan2(f, j) * 57.2957763671875);
        matrix.translate(0.0, 0.0, 0.0);
        matrix.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(k - 90.0f));
        matrix.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(l));
        float m = 0.0f;
        float n = 0.125f;
        float o = 0.0f;
        float p = 0.0625f;
        float q = 0.03125f;
        matrix.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(45.0f));
        matrix.scale(0.03125f, 0.03125f, 0.03125f);
        matrix.translate(2.5, 0.0, 0.0);
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEntityCutoutNoCull(field_20529));
        for (int r = 0; r < 4; ++r) {
            matrix.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(90.0f));
            MatrixStack.Entry entry = matrix.peek();
            Matrix4f matrix4f = entry.getModel();
            Matrix3f matrix3f = entry.getNormal();
            StuckStingersFeatureRenderer.method_23295(vertexConsumer, matrix4f, matrix3f, -4.5f, -1, 0.0f, 0.0f, i);
            StuckStingersFeatureRenderer.method_23295(vertexConsumer, matrix4f, matrix3f, 4.5f, -1, 0.125f, 0.0f, i);
            StuckStingersFeatureRenderer.method_23295(vertexConsumer, matrix4f, matrix3f, 4.5f, 1, 0.125f, 0.0625f, i);
            StuckStingersFeatureRenderer.method_23295(vertexConsumer, matrix4f, matrix3f, -4.5f, 1, 0.0f, 0.0625f, i);
        }
    }

    private static void method_23295(VertexConsumer vertexConsumer, Matrix4f matrix4f, Matrix3f matrix3f, float f, int i, float g, float h, int j) {
        vertexConsumer.vertex(matrix4f, f, i, 0.0f).color(255, 255, 255, 255).texture(g, h).overlay(OverlayTexture.DEFAULT_UV).light(j).normal(matrix3f, 0.0f, 1.0f, 0.0f).next();
    }
}

