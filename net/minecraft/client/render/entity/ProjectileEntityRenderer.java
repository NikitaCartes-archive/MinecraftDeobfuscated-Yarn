/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.LayeredVertexConsumerStorage;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public abstract class ProjectileEntityRenderer<T extends ProjectileEntity>
extends EntityRenderer<T> {
    public ProjectileEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher);
    }

    public void method_3875(T projectileEntity, double d, double e, double f, float g, float h, MatrixStack matrixStack, LayeredVertexConsumerStorage layeredVertexConsumerStorage) {
        matrixStack.push();
        matrixStack.multiply(Vector3f.POSITIVE_Y.getRotationQuaternion(MathHelper.lerp(h, ((ProjectileEntity)projectileEntity).prevYaw, ((ProjectileEntity)projectileEntity).yaw) - 90.0f));
        matrixStack.multiply(Vector3f.POSITIVE_Z.getRotationQuaternion(MathHelper.lerp(h, ((ProjectileEntity)projectileEntity).prevPitch, ((ProjectileEntity)projectileEntity).pitch)));
        boolean i = false;
        float j = 0.0f;
        float k = 0.5f;
        float l = 0.0f;
        float m = 0.15625f;
        float n = 0.0f;
        float o = 0.15625f;
        float p = 0.15625f;
        float q = 0.3125f;
        float r = 0.05625f;
        float s = (float)((ProjectileEntity)projectileEntity).shake - h;
        if (s > 0.0f) {
            float t = -MathHelper.sin(s * 3.0f) * s;
            matrixStack.multiply(Vector3f.POSITIVE_Z.getRotationQuaternion(t));
        }
        matrixStack.multiply(Vector3f.POSITIVE_X.getRotationQuaternion(45.0f));
        matrixStack.scale(0.05625f, 0.05625f, 0.05625f);
        matrixStack.translate(-4.0, 0.0, 0.0);
        int u = ((Entity)projectileEntity).getLightmapCoordinates();
        VertexConsumer vertexConsumer = layeredVertexConsumerStorage.getBuffer(RenderLayer.getEntityCutoutNoCull(this.getTexture(projectileEntity)));
        Matrix4f matrix4f = matrixStack.peek();
        this.method_23153(matrix4f, vertexConsumer, -7, -2, -2, 0.0f, 0.15625f, 1, 0, 0, u);
        this.method_23153(matrix4f, vertexConsumer, -7, -2, 2, 0.15625f, 0.15625f, 1, 0, 0, u);
        this.method_23153(matrix4f, vertexConsumer, -7, 2, 2, 0.15625f, 0.3125f, 1, 0, 0, u);
        this.method_23153(matrix4f, vertexConsumer, -7, 2, -2, 0.0f, 0.3125f, 1, 0, 0, u);
        this.method_23153(matrix4f, vertexConsumer, -7, 2, -2, 0.0f, 0.15625f, -1, 0, 0, u);
        this.method_23153(matrix4f, vertexConsumer, -7, 2, 2, 0.15625f, 0.15625f, -1, 0, 0, u);
        this.method_23153(matrix4f, vertexConsumer, -7, -2, 2, 0.15625f, 0.3125f, -1, 0, 0, u);
        this.method_23153(matrix4f, vertexConsumer, -7, -2, -2, 0.0f, 0.3125f, -1, 0, 0, u);
        for (int v = 0; v < 4; ++v) {
            matrixStack.multiply(Vector3f.POSITIVE_X.getRotationQuaternion(90.0f));
            this.method_23153(matrix4f, vertexConsumer, -8, -2, 0, 0.0f, 0.0f, 0, 1, 0, u);
            this.method_23153(matrix4f, vertexConsumer, 8, -2, 0, 0.5f, 0.0f, 0, 1, 0, u);
            this.method_23153(matrix4f, vertexConsumer, 8, 2, 0, 0.5f, 0.15625f, 0, 1, 0, u);
            this.method_23153(matrix4f, vertexConsumer, -8, 2, 0, 0.0f, 0.15625f, 0, 1, 0, u);
        }
        matrixStack.pop();
        super.render(projectileEntity, d, e, f, g, h, matrixStack, layeredVertexConsumerStorage);
    }

    public void method_23153(Matrix4f matrix4f, VertexConsumer vertexConsumer, int i, int j, int k, float f, float g, int l, int m, int n, int o) {
        vertexConsumer.vertex(matrix4f, i, j, k).color(255, 255, 255, 255).texture(f, g).defaultOverlay(OverlayTexture.DEFAULT_UV).light(o).normal(l, n, m).next();
    }
}

