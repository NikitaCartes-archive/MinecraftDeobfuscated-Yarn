/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5617;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
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
    public MobEntityRenderer(class_5617.class_5618 arg, M entityModel, float f) {
        super(arg, entityModel, f);
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
        int v;
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
        for (v = 0; v <= 24; ++v) {
            MobEntityRenderer.method_23187(vertexConsumer, matrix4f, k, l, m, r, s, t, u, 0.025f, 0.025f, p, q, v, false);
        }
        for (v = 24; v >= 0; --v) {
            MobEntityRenderer.method_23187(vertexConsumer, matrix4f, k, l, m, r, s, t, u, 0.025f, 0.0f, p, q, v, true);
        }
        matrixStack.pop();
    }

    private static void method_23187(VertexConsumer vertexConsumer, Matrix4f matrix4f, float f, float g, float h, int i, int j, int k, int l, float m, float n, float o, float p, int q, boolean bl) {
        float r = (float)q / 24.0f;
        int s = (int)MathHelper.lerp(r, i, j);
        int t = (int)MathHelper.lerp(r, k, l);
        int u = LightmapTextureManager.pack(s, t);
        float v = q % 2 == (bl ? 1 : 0) ? 0.7f : 1.0f;
        float w = 0.5f * v;
        float x = 0.4f * v;
        float y = 0.3f * v;
        float z = f * r;
        float aa = g > 0.0f ? g * r * r : g - g * (1.0f - r) * (1.0f - r);
        float ab = h * r;
        vertexConsumer.vertex(matrix4f, z - o, aa + n, ab + p).color(w, x, y, 1.0f).light(u).next();
        vertexConsumer.vertex(matrix4f, z + o, aa + m - n, ab - p).color(w, x, y, 1.0f).light(u).next();
    }
}

