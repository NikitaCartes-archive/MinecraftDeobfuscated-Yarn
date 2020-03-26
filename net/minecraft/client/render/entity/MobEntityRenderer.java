/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.AbstractDecorationEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.world.LightType;

@Environment(value=EnvType.CLIENT)
public abstract class MobEntityRenderer<T extends MobEntity, M extends EntityModel<T>>
extends LivingEntityRenderer<T, M> {
    public MobEntityRenderer(EntityRenderDispatcher entityRenderDispatcher, M entityModel, float f) {
        super(entityRenderDispatcher, entityModel, f);
    }

    @Override
    protected boolean hasLabel(T mobEntity) {
        return super.hasLabel(mobEntity) && (((LivingEntity)mobEntity).shouldRenderName() || ((Entity)mobEntity).hasCustomName() && mobEntity == this.dispatcher.targetedEntity);
    }

    @Override
    public boolean shouldRender(T mobEntity, Frustum frustum, double d, double e, double f) {
        if (super.shouldRender(mobEntity, frustum, d, e, f)) {
            return true;
        }
        Entity entity = ((MobEntity)mobEntity).getHoldingEntity();
        if (entity != null) {
            return frustum.isVisible(entity.getVisibilityBoundingBox());
        }
        return false;
    }

    @Override
    public void render(T mobEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        super.render(mobEntity, f, g, matrixStack, vertexConsumerProvider, i);
        Entity entity = ((MobEntity)mobEntity).getHoldingEntity();
        if (entity == null) {
            return;
        }
        this.method_4073(mobEntity, g, matrixStack, vertexConsumerProvider, entity);
    }

    private <E extends Entity> void method_4073(T mobEntity, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, E entity) {
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
        double n = (double)(MathHelper.lerp(f, ((MobEntity)mobEntity).bodyYaw, ((MobEntity)mobEntity).prevBodyYaw) * ((float)Math.PI / 180)) + 1.5707963267948966;
        g = Math.cos(n) * (double)((Entity)mobEntity).getWidth() * 0.4;
        h = Math.sin(n) * (double)((Entity)mobEntity).getWidth() * 0.4;
        double o = MathHelper.lerp((double)f, ((MobEntity)mobEntity).prevX, ((Entity)mobEntity).getX()) + g;
        double p = MathHelper.lerp((double)f, ((MobEntity)mobEntity).prevY, ((Entity)mobEntity).getY());
        double q = MathHelper.lerp((double)f, ((MobEntity)mobEntity).prevZ, ((Entity)mobEntity).getZ()) + h;
        matrixStack.translate(g, -(1.6 - (double)((Entity)mobEntity).getHeight()) * 0.5, h);
        float r = (float)(k - o);
        float s = (float)(l - p);
        float t = (float)(m - q);
        float u = 0.025f;
        VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayer.getLeash());
        Matrix4f matrix4f = matrixStack.peek().getModel();
        float v = MathHelper.fastInverseSqrt(r * r + t * t) * 0.025f / 2.0f;
        float w = t * v;
        float x = r * v;
        int y = this.getBlockLight(mobEntity, f);
        int z = this.dispatcher.getRenderer(entity).getBlockLight(entity, f);
        int aa = ((MobEntity)mobEntity).world.getLightLevel(LightType.SKY, new BlockPos(((Entity)mobEntity).getCameraPosVec(f)));
        int ab = ((MobEntity)mobEntity).world.getLightLevel(LightType.SKY, new BlockPos(entity.getCameraPosVec(f)));
        MobEntityRenderer.method_23186(vertexConsumer, matrix4f, r, s, t, y, z, aa, ab, 0.025f, 0.025f, w, x);
        MobEntityRenderer.method_23186(vertexConsumer, matrix4f, r, s, t, y, z, aa, ab, 0.025f, 0.0f, w, x);
        matrixStack.pop();
    }

    public static void method_23186(VertexConsumer vertexConsumer, Matrix4f matrix4f, float f, float g, float h, int i, int j, int k, int l, float m, float n, float o, float p) {
        int q = 24;
        for (int r = 0; r < 24; ++r) {
            float s = (float)r / 23.0f;
            int t = (int)MathHelper.lerp(s, i, j);
            int u = (int)MathHelper.lerp(s, k, l);
            int v = LightmapTextureManager.pack(t, u);
            MobEntityRenderer.method_23187(vertexConsumer, matrix4f, v, f, g, h, m, n, 24, r, false, o, p);
            MobEntityRenderer.method_23187(vertexConsumer, matrix4f, v, f, g, h, m, n, 24, r + 1, true, o, p);
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
}

