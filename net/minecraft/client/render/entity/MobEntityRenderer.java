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
import net.minecraft.client.render.entity.EntityRendererFactory;
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
    public static final int field_32940 = 24;

    public MobEntityRenderer(EntityRendererFactory.Context context, M entityModel, float f) {
        super(context, entityModel, f);
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

    private <E extends Entity> void method_4073(T entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider provider, E holdingEntity) {
        int u;
        matrices.push();
        Vec3d vec3d = holdingEntity.method_30951(tickDelta);
        double d = (double)(MathHelper.lerp(tickDelta, ((MobEntity)entity).bodyYaw, ((MobEntity)entity).prevBodyYaw) * ((float)Math.PI / 180)) + 1.5707963267948966;
        Vec3d vec3d2 = ((Entity)entity).getLeashOffset();
        double e = Math.cos(d) * vec3d2.z + Math.sin(d) * vec3d2.x;
        double f = Math.sin(d) * vec3d2.z - Math.cos(d) * vec3d2.x;
        double g = MathHelper.lerp((double)tickDelta, ((MobEntity)entity).prevX, ((Entity)entity).getX()) + e;
        double h = MathHelper.lerp((double)tickDelta, ((MobEntity)entity).prevY, ((Entity)entity).getY()) + vec3d2.y;
        double i = MathHelper.lerp((double)tickDelta, ((MobEntity)entity).prevZ, ((Entity)entity).getZ()) + f;
        matrices.translate(e, vec3d2.y, f);
        float j = (float)(vec3d.x - g);
        float k = (float)(vec3d.y - h);
        float l = (float)(vec3d.z - i);
        float m = 0.025f;
        VertexConsumer vertexConsumer = provider.getBuffer(RenderLayer.getLeash());
        Matrix4f matrix4f = matrices.peek().getModel();
        float n = MathHelper.fastInverseSqrt(j * j + l * l) * 0.025f / 2.0f;
        float o = l * n;
        float p = j * n;
        BlockPos blockPos = new BlockPos(((Entity)entity).getCameraPosVec(tickDelta));
        BlockPos blockPos2 = new BlockPos(holdingEntity.getCameraPosVec(tickDelta));
        int q = this.getBlockLight(entity, blockPos);
        int r = this.dispatcher.getRenderer(holdingEntity).getBlockLight(holdingEntity, blockPos2);
        int s = ((MobEntity)entity).world.getLightLevel(LightType.SKY, blockPos);
        int t = ((MobEntity)entity).world.getLightLevel(LightType.SKY, blockPos2);
        for (u = 0; u <= 24; ++u) {
            MobEntityRenderer.method_23187(vertexConsumer, matrix4f, j, k, l, q, r, s, t, 0.025f, 0.025f, o, p, u, false);
        }
        for (u = 24; u >= 0; --u) {
            MobEntityRenderer.method_23187(vertexConsumer, matrix4f, j, k, l, q, r, s, t, 0.025f, 0.0f, o, p, u, true);
        }
        matrices.pop();
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

