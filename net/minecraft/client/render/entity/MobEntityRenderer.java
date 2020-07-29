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
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3d;
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
        Vec3d vec3d = entity.method_30951(f);
        double d = (double)(MathHelper.lerp(f, ((MobEntity)mobEntity).bodyYaw, ((MobEntity)mobEntity).prevBodyYaw) * ((float)Math.PI / 180)) + 1.5707963267948966;
        Vec3d vec3d2 = ((Entity)mobEntity).method_29919();
        double e = Math.cos(d) * vec3d2.z + Math.sin(d) * vec3d2.x;
        double g = Math.sin(d) * vec3d2.z - Math.cos(d) * vec3d2.x;
        double h = MathHelper.lerp((double)f, ((MobEntity)mobEntity).prevX, ((Entity)mobEntity).getX()) + e;
        double i = MathHelper.lerp((double)f, ((MobEntity)mobEntity).prevY, ((Entity)mobEntity).getY()) + vec3d2.y;
        double j = MathHelper.lerp((double)f, ((MobEntity)mobEntity).prevZ, ((Entity)mobEntity).getZ()) + g;
        matrixStack.translate(e, vec3d2.y, g);
        float k = (float)(vec3d.x - h);
        float l = (float)(vec3d.y - i);
        float m = (float)(vec3d.z - j);
        float n = 0.025f;
        VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayer.getLeash());
        Matrix4f matrix4f = matrixStack.peek().getModel();
        float o = MathHelper.fastInverseSqrt(k * k + m * m) * 0.025f / 2.0f;
        float p = m * o;
        float q = k * o;
        BlockPos blockPos = new BlockPos(((Entity)mobEntity).getCameraPosVec(f));
        BlockPos blockPos2 = new BlockPos(entity.getCameraPosVec(f));
        int r = this.getBlockLight(mobEntity, blockPos);
        int s = this.dispatcher.getRenderer(entity).getBlockLight(entity, blockPos2);
        int t = ((MobEntity)mobEntity).world.getLightLevel(LightType.SKY, blockPos);
        int u = ((MobEntity)mobEntity).world.getLightLevel(LightType.SKY, blockPos2);
        MobEntityRenderer.method_23186(vertexConsumer, matrix4f, k, l, m, r, s, t, u, 0.025f, 0.025f, p, q);
        MobEntityRenderer.method_23186(vertexConsumer, matrix4f, k, l, m, r, s, t, u, 0.025f, 0.0f, p, q);
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
        float u = g > 0.0f ? g * s * s : g - g * (1.0f - s) * (1.0f - s);
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

