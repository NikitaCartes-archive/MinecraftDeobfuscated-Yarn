/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.LayeredVertexConsumerStorage;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.AbstractDecorationEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public abstract class MobEntityRenderer<T extends MobEntity, M extends EntityModel<T>>
extends LivingEntityRenderer<T, M> {
    public MobEntityRenderer(EntityRenderDispatcher entityRenderDispatcher, M entityModel, float f) {
        super(entityRenderDispatcher, entityModel, f);
    }

    protected boolean method_4071(T mobEntity) {
        return super.method_4055(mobEntity) && (((LivingEntity)mobEntity).shouldRenderName() || ((Entity)mobEntity).hasCustomName() && mobEntity == this.renderManager.targetedEntity);
    }

    public boolean method_4068(T mobEntity, Frustum frustum, double d, double e, double f) {
        if (super.isVisible(mobEntity, frustum, d, e, f)) {
            return true;
        }
        Entity entity = ((MobEntity)mobEntity).getHoldingEntity();
        if (entity != null) {
            return frustum.isVisible(entity.getVisibilityBoundingBox());
        }
        return false;
    }

    public void method_4072(T mobEntity, double d, double e, double f, float g, float h, MatrixStack matrixStack, LayeredVertexConsumerStorage layeredVertexConsumerStorage) {
        super.method_4054(mobEntity, d, e, f, g, h, matrixStack, layeredVertexConsumerStorage);
        Entity entity = ((MobEntity)mobEntity).getHoldingEntity();
        if (entity == null) {
            return;
        }
        MobEntityRenderer.method_4073(mobEntity, h, matrixStack, layeredVertexConsumerStorage, entity);
    }

    public static void method_4073(MobEntity mobEntity, float f, MatrixStack matrixStack, LayeredVertexConsumerStorage layeredVertexConsumerStorage, Entity entity) {
        matrixStack.push();
        double d = MathHelper.lerp(f * 0.5f, entity.yaw, entity.prevYaw) * ((float)Math.PI / 180);
        double e = MathHelper.lerp(f * 0.5f, entity.pitch, entity.prevPitch) * ((float)Math.PI / 180);
        double g = Math.cos(d);
        double h = Math.sin(d);
        double i = Math.sin(e);
        if (entity instanceof AbstractDecorationEntity) {
            g = 0.0;
            h = 0.0;
            i = -1.0;
        }
        double j = Math.cos(e);
        double k = MathHelper.lerp((double)f, entity.prevX, entity.getX()) - g * 0.7 - h * 0.5 * j;
        double l = MathHelper.lerp((double)f, entity.prevY + (double)entity.getStandingEyeHeight() * 0.7, entity.getY() + (double)entity.getStandingEyeHeight() * 0.7) - i * 0.5 - 0.25;
        double m = MathHelper.lerp((double)f, entity.prevZ, entity.getZ()) - h * 0.7 + g * 0.5 * j;
        double n = (double)(MathHelper.lerp(f, mobEntity.bodyYaw, mobEntity.prevBodyYaw) * ((float)Math.PI / 180)) + 1.5707963267948966;
        g = Math.cos(n) * (double)mobEntity.getWidth() * 0.4;
        h = Math.sin(n) * (double)mobEntity.getWidth() * 0.4;
        double o = MathHelper.lerp((double)f, mobEntity.prevX, mobEntity.getX()) + g;
        double p = MathHelper.lerp((double)f, mobEntity.prevY, mobEntity.getY());
        double q = MathHelper.lerp((double)f, mobEntity.prevZ, mobEntity.getZ()) + h;
        matrixStack.translate(g, -(1.6 - (double)mobEntity.getHeight()) * 0.5, h);
        float r = (float)(k - o);
        float s = (float)(l - p);
        float t = (float)(m - q);
        float u = 0.025f;
        VertexConsumer vertexConsumer = layeredVertexConsumerStorage.getBuffer(RenderLayer.getLeash());
        Matrix4f matrix4f = matrixStack.peek();
        float v = MathHelper.fastInverseSqrt(r * r + t * t) * 0.025f / 2.0f;
        float w = t * v;
        float x = r * v;
        int y = mobEntity.getLightmapCoordinates();
        int z = entity.getLightmapCoordinates();
        MobEntityRenderer.method_23186(vertexConsumer, matrix4f, y, z, r, s, t, 0.025f, 0.025f, w, x);
        MobEntityRenderer.method_23186(vertexConsumer, matrix4f, y, z, r, s, t, 0.025f, 0.0f, w, x);
        matrixStack.pop();
    }

    public static void method_23186(VertexConsumer vertexConsumer, Matrix4f matrix4f, int i, int j, float f, float g, float h, float k, float l, float m, float n) {
        int o = 24;
        int p = LightmapTextureManager.method_23686(i);
        int q = LightmapTextureManager.method_23686(j);
        int r = LightmapTextureManager.method_23688(i);
        int s = LightmapTextureManager.method_23688(j);
        for (int t = 0; t < 24; ++t) {
            float u = (float)t / 23.0f;
            int v = (int)MathHelper.lerp(u, p, q);
            int w = (int)MathHelper.lerp(u, r, s);
            int x = LightmapTextureManager.method_23687(v, w);
            MobEntityRenderer.method_23187(vertexConsumer, matrix4f, x, f, g, h, k, l, 24, t, false, m, n);
            MobEntityRenderer.method_23187(vertexConsumer, matrix4f, x, f, g, h, k, l, 24, t + 1, true, m, n);
        }
    }

    public static void method_23187(VertexConsumer vertexConsumer, Matrix4f matrix4f, int i, float f, float g, float h, float j, float k, int l, int m, boolean bl, float n, float o) {
        float p = 0.5f;
        float q = 0.4f;
        float r = 0.3f;
        if (m % 2 == 0) {
            p *= 0.7f;
            q *= 0.7f;
            r *= 0.7f;
        }
        float s = (float)m / (float)l;
        float t = f * s;
        float u = g * (s * s + s) * 0.5f + ((float)l - (float)m) / ((float)l * 0.75f) + 0.125f;
        float v = h * s;
        if (!bl) {
            vertexConsumer.vertex(matrix4f, t + n, u + j - k, v - o).color(p, q, r, 1.0f).light(i).next();
        }
        vertexConsumer.vertex(matrix4f, t - n, u + k, v + o).color(p, q, r, 1.0f).light(i).next();
        if (bl) {
            vertexConsumer.vertex(matrix4f, t + n, u + j - k, v - o).color(p, q, r, 1.0f).light(i).next();
        }
    }

    @Override
    protected /* synthetic */ boolean method_4055(LivingEntity livingEntity) {
        return this.method_4071((MobEntity)livingEntity);
    }
}

