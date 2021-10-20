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
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix3f;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3f;

@Environment(value=EnvType.CLIENT)
public class StuckStingersFeatureRenderer<T extends LivingEntity, M extends PlayerEntityModel<T>>
extends StuckObjectsFeatureRenderer<T, M> {
    private static final Identifier TEXTURE = new Identifier("textures/entity/bee/bee_stinger.png");

    public StuckStingersFeatureRenderer(LivingEntityRenderer<T, M> livingEntityRenderer) {
        super(livingEntityRenderer);
    }

    @Override
    protected int getObjectCount(T entity) {
        return ((LivingEntity)entity).getStingerCount();
    }

    @Override
    protected void renderObject(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, Entity entity, float directionX, float directionY, float directionZ, float tickDelta) {
        float f = MathHelper.sqrt(directionX * directionX + directionZ * directionZ);
        float g = (float)(Math.atan2(directionX, directionZ) * 57.2957763671875);
        float h = (float)(Math.atan2(directionY, f) * 57.2957763671875);
        matrices.translate(0.0, 0.0, 0.0);
        matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(g - 90.0f));
        matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(h));
        float i = 0.0f;
        float j = 0.125f;
        float k = 0.0f;
        float l = 0.0625f;
        float m = 0.03125f;
        matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(45.0f));
        matrices.scale(0.03125f, 0.03125f, 0.03125f);
        matrices.translate(2.5, 0.0, 0.0);
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEntityCutoutNoCull(TEXTURE));
        for (int n = 0; n < 4; ++n) {
            matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(90.0f));
            MatrixStack.Entry entry = matrices.peek();
            Matrix4f matrix4f = entry.getPositionMatrix();
            Matrix3f matrix3f = entry.getNormalMatrix();
            StuckStingersFeatureRenderer.produceVertex(vertexConsumer, matrix4f, matrix3f, -4.5f, -1, 0.0f, 0.0f, light);
            StuckStingersFeatureRenderer.produceVertex(vertexConsumer, matrix4f, matrix3f, 4.5f, -1, 0.125f, 0.0f, light);
            StuckStingersFeatureRenderer.produceVertex(vertexConsumer, matrix4f, matrix3f, 4.5f, 1, 0.125f, 0.0625f, light);
            StuckStingersFeatureRenderer.produceVertex(vertexConsumer, matrix4f, matrix3f, -4.5f, 1, 0.0f, 0.0625f, light);
        }
    }

    private static void produceVertex(VertexConsumer vertexConsumer, Matrix4f vertexTransform, Matrix3f normalTransform, float x, int y, float u, float v, int light) {
        vertexConsumer.vertex(vertexTransform, x, y, 0.0f).color(255, 255, 255, 255).texture(u, v).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(normalTransform, 0.0f, 1.0f, 0.0f).next();
    }
}

