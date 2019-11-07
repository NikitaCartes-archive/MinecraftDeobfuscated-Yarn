/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.GuardianEntityModel;
import net.minecraft.client.util.math.Matrix3f;
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.GuardianEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

@Environment(value=EnvType.CLIENT)
public class GuardianEntityRenderer
extends MobEntityRenderer<GuardianEntity, GuardianEntityModel> {
    private static final Identifier SKIN = new Identifier("textures/entity/guardian.png");
    private static final Identifier EXPLOSION_BEAM_TEX = new Identifier("textures/entity/guardian_beam.png");

    public GuardianEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        this(entityRenderDispatcher, 0.5f);
    }

    protected GuardianEntityRenderer(EntityRenderDispatcher entityRenderDispatcher, float f) {
        super(entityRenderDispatcher, new GuardianEntityModel(), f);
    }

    public boolean method_3978(GuardianEntity guardianEntity, Frustum frustum, double d, double e, double f) {
        LivingEntity livingEntity;
        if (super.method_4068(guardianEntity, frustum, d, e, f)) {
            return true;
        }
        if (guardianEntity.hasBeamTarget() && (livingEntity = guardianEntity.getBeamTarget()) != null) {
            Vec3d vec3d = this.fromLerpedPosition(livingEntity, (double)livingEntity.getHeight() * 0.5, 1.0f);
            Vec3d vec3d2 = this.fromLerpedPosition(guardianEntity, guardianEntity.getStandingEyeHeight(), 1.0f);
            return frustum.isVisible(new Box(vec3d2.x, vec3d2.y, vec3d2.z, vec3d.x, vec3d.y, vec3d.z));
        }
        return false;
    }

    private Vec3d fromLerpedPosition(LivingEntity livingEntity, double d, float f) {
        double e = MathHelper.lerp((double)f, livingEntity.prevRenderX, livingEntity.getX());
        double g = MathHelper.lerp((double)f, livingEntity.prevRenderY, livingEntity.getY()) + d;
        double h = MathHelper.lerp((double)f, livingEntity.prevRenderZ, livingEntity.getZ());
        return new Vec3d(e, g, h);
    }

    public void method_3977(GuardianEntity guardianEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        super.method_4072(guardianEntity, f, g, matrixStack, vertexConsumerProvider, i);
        LivingEntity livingEntity = guardianEntity.getBeamTarget();
        if (livingEntity != null) {
            float h = guardianEntity.getBeamProgress(g);
            float j = (float)guardianEntity.world.getTime() + g;
            float k = j * 0.5f % 1.0f;
            float l = guardianEntity.getStandingEyeHeight();
            matrixStack.push();
            matrixStack.translate(0.0, l, 0.0);
            Vec3d vec3d = this.fromLerpedPosition(livingEntity, (double)livingEntity.getHeight() * 0.5, g);
            Vec3d vec3d2 = this.fromLerpedPosition(guardianEntity, l, g);
            Vec3d vec3d3 = vec3d.subtract(vec3d2);
            float m = (float)(vec3d3.length() + 1.0);
            vec3d3 = vec3d3.normalize();
            float n = (float)Math.acos(vec3d3.y);
            float o = (float)Math.atan2(vec3d3.z, vec3d3.x);
            matrixStack.multiply(Vector3f.POSITIVE_Y.getRotationQuaternion((1.5707964f - o) * 57.295776f));
            matrixStack.multiply(Vector3f.POSITIVE_X.getRotationQuaternion(n * 57.295776f));
            boolean p = true;
            float q = j * 0.05f * -1.5f;
            float r = h * h;
            int s = 64 + (int)(r * 191.0f);
            int t = 32 + (int)(r * 191.0f);
            int u = 128 - (int)(r * 64.0f);
            float v = 0.2f;
            float w = 0.282f;
            float x = MathHelper.cos(q + 2.3561945f) * 0.282f;
            float y = MathHelper.sin(q + 2.3561945f) * 0.282f;
            float z = MathHelper.cos(q + 0.7853982f) * 0.282f;
            float aa = MathHelper.sin(q + 0.7853982f) * 0.282f;
            float ab = MathHelper.cos(q + 3.926991f) * 0.282f;
            float ac = MathHelper.sin(q + 3.926991f) * 0.282f;
            float ad = MathHelper.cos(q + 5.4977875f) * 0.282f;
            float ae = MathHelper.sin(q + 5.4977875f) * 0.282f;
            float af = MathHelper.cos(q + (float)Math.PI) * 0.2f;
            float ag = MathHelper.sin(q + (float)Math.PI) * 0.2f;
            float ah = MathHelper.cos(q + 0.0f) * 0.2f;
            float ai = MathHelper.sin(q + 0.0f) * 0.2f;
            float aj = MathHelper.cos(q + 1.5707964f) * 0.2f;
            float ak = MathHelper.sin(q + 1.5707964f) * 0.2f;
            float al = MathHelper.cos(q + 4.712389f) * 0.2f;
            float am = MathHelper.sin(q + 4.712389f) * 0.2f;
            float an = m;
            float ao = 0.0f;
            float ap = 0.4999f;
            float aq = -1.0f + k;
            float ar = m * 2.5f + aq;
            VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayer.getEntityCutoutNoCull(EXPLOSION_BEAM_TEX));
            MatrixStack.Entry entry = matrixStack.method_23760();
            Matrix4f matrix4f = entry.method_23761();
            Matrix3f matrix3f = entry.method_23762();
            GuardianEntityRenderer.method_23173(vertexConsumer, matrix4f, matrix3f, af, an, ag, s, t, u, 0.4999f, ar);
            GuardianEntityRenderer.method_23173(vertexConsumer, matrix4f, matrix3f, af, 0.0f, ag, s, t, u, 0.4999f, aq);
            GuardianEntityRenderer.method_23173(vertexConsumer, matrix4f, matrix3f, ah, 0.0f, ai, s, t, u, 0.0f, aq);
            GuardianEntityRenderer.method_23173(vertexConsumer, matrix4f, matrix3f, ah, an, ai, s, t, u, 0.0f, ar);
            GuardianEntityRenderer.method_23173(vertexConsumer, matrix4f, matrix3f, aj, an, ak, s, t, u, 0.4999f, ar);
            GuardianEntityRenderer.method_23173(vertexConsumer, matrix4f, matrix3f, aj, 0.0f, ak, s, t, u, 0.4999f, aq);
            GuardianEntityRenderer.method_23173(vertexConsumer, matrix4f, matrix3f, al, 0.0f, am, s, t, u, 0.0f, aq);
            GuardianEntityRenderer.method_23173(vertexConsumer, matrix4f, matrix3f, al, an, am, s, t, u, 0.0f, ar);
            float as = 0.0f;
            if (guardianEntity.age % 2 == 0) {
                as = 0.5f;
            }
            GuardianEntityRenderer.method_23173(vertexConsumer, matrix4f, matrix3f, x, an, y, s, t, u, 0.5f, as + 0.5f);
            GuardianEntityRenderer.method_23173(vertexConsumer, matrix4f, matrix3f, z, an, aa, s, t, u, 1.0f, as + 0.5f);
            GuardianEntityRenderer.method_23173(vertexConsumer, matrix4f, matrix3f, ad, an, ae, s, t, u, 1.0f, as);
            GuardianEntityRenderer.method_23173(vertexConsumer, matrix4f, matrix3f, ab, an, ac, s, t, u, 0.5f, as);
            matrixStack.pop();
        }
    }

    private static void method_23173(VertexConsumer vertexConsumer, Matrix4f matrix4f, Matrix3f matrix3f, float f, float g, float h, int i, int j, int k, float l, float m) {
        vertexConsumer.vertex(matrix4f, f, g, h).color(i, j, k, 255).texture(l, m).overlay(OverlayTexture.DEFAULT_UV).light(0xF000F0).method_23763(matrix3f, 0.0f, 1.0f, 0.0f).next();
    }

    public Identifier method_3976(GuardianEntity guardianEntity) {
        return SKIN;
    }
}

