/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.VisibleRegion;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.GuardianEntityModel;
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

    @Override
    public boolean isVisible(GuardianEntity guardianEntity, VisibleRegion visibleRegion, double d, double e, double f) {
        LivingEntity livingEntity;
        if (super.isVisible(guardianEntity, visibleRegion, d, e, f)) {
            return true;
        }
        if (guardianEntity.hasBeamTarget() && (livingEntity = guardianEntity.getBeamTarget()) != null) {
            Vec3d vec3d = this.fromLerpedPosition(livingEntity, (double)livingEntity.getHeight() * 0.5, 1.0f);
            Vec3d vec3d2 = this.fromLerpedPosition(guardianEntity, guardianEntity.getStandingEyeHeight(), 1.0f);
            if (visibleRegion.intersects(new Box(vec3d2.x, vec3d2.y, vec3d2.z, vec3d.x, vec3d.y, vec3d.z))) {
                return true;
            }
        }
        return false;
    }

    private Vec3d fromLerpedPosition(LivingEntity livingEntity, double d, float f) {
        double e = MathHelper.lerp((double)f, livingEntity.lastRenderX, livingEntity.x);
        double g = MathHelper.lerp((double)f, livingEntity.lastRenderY, livingEntity.y) + d;
        double h = MathHelper.lerp((double)f, livingEntity.lastRenderZ, livingEntity.z);
        return new Vec3d(e, g, h);
    }

    @Override
    public void render(GuardianEntity guardianEntity, double d, double e, double f, float g, float h) {
        super.render(guardianEntity, d, e, f, g, h);
        LivingEntity livingEntity = guardianEntity.getBeamTarget();
        if (livingEntity != null) {
            float i = guardianEntity.getBeamProgress(h);
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferBuilder = tessellator.getBuffer();
            this.bindTexture(EXPLOSION_BEAM_TEX);
            GlStateManager.texParameter(3553, 10242, 10497);
            GlStateManager.texParameter(3553, 10243, 10497);
            GlStateManager.disableLighting();
            GlStateManager.disableCull();
            GlStateManager.disableBlend();
            GlStateManager.depthMask(true);
            float j = 240.0f;
            GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, 240.0f, 240.0f);
            GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            float k = (float)guardianEntity.world.getTime() + h;
            float l = k * 0.5f % 1.0f;
            float m = guardianEntity.getStandingEyeHeight();
            GlStateManager.pushMatrix();
            GlStateManager.translatef((float)d, (float)e + m, (float)f);
            Vec3d vec3d = this.fromLerpedPosition(livingEntity, (double)livingEntity.getHeight() * 0.5, h);
            Vec3d vec3d2 = this.fromLerpedPosition(guardianEntity, m, h);
            Vec3d vec3d3 = vec3d.subtract(vec3d2);
            double n = vec3d3.length() + 1.0;
            vec3d3 = vec3d3.normalize();
            float o = (float)Math.acos(vec3d3.y);
            float p = (float)Math.atan2(vec3d3.z, vec3d3.x);
            GlStateManager.rotatef((1.5707964f - p) * 57.295776f, 0.0f, 1.0f, 0.0f);
            GlStateManager.rotatef(o * 57.295776f, 1.0f, 0.0f, 0.0f);
            boolean q = true;
            double r = (double)k * 0.05 * -1.5;
            bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
            float s = i * i;
            int t = 64 + (int)(s * 191.0f);
            int u = 32 + (int)(s * 191.0f);
            int v = 128 - (int)(s * 64.0f);
            double w = 0.2;
            double x = 0.282;
            double y = 0.0 + Math.cos(r + 2.356194490192345) * 0.282;
            double z = 0.0 + Math.sin(r + 2.356194490192345) * 0.282;
            double aa = 0.0 + Math.cos(r + 0.7853981633974483) * 0.282;
            double ab = 0.0 + Math.sin(r + 0.7853981633974483) * 0.282;
            double ac = 0.0 + Math.cos(r + 3.9269908169872414) * 0.282;
            double ad = 0.0 + Math.sin(r + 3.9269908169872414) * 0.282;
            double ae = 0.0 + Math.cos(r + 5.497787143782138) * 0.282;
            double af = 0.0 + Math.sin(r + 5.497787143782138) * 0.282;
            double ag = 0.0 + Math.cos(r + Math.PI) * 0.2;
            double ah = 0.0 + Math.sin(r + Math.PI) * 0.2;
            double ai = 0.0 + Math.cos(r + 0.0) * 0.2;
            double aj = 0.0 + Math.sin(r + 0.0) * 0.2;
            double ak = 0.0 + Math.cos(r + 1.5707963267948966) * 0.2;
            double al = 0.0 + Math.sin(r + 1.5707963267948966) * 0.2;
            double am = 0.0 + Math.cos(r + 4.71238898038469) * 0.2;
            double an = 0.0 + Math.sin(r + 4.71238898038469) * 0.2;
            double ao = n;
            double ap = 0.0;
            double aq = 0.4999;
            double ar = -1.0f + l;
            double as = n * 2.5 + ar;
            bufferBuilder.vertex(ag, ao, ah).texture(0.4999, as).color(t, u, v, 255).next();
            bufferBuilder.vertex(ag, 0.0, ah).texture(0.4999, ar).color(t, u, v, 255).next();
            bufferBuilder.vertex(ai, 0.0, aj).texture(0.0, ar).color(t, u, v, 255).next();
            bufferBuilder.vertex(ai, ao, aj).texture(0.0, as).color(t, u, v, 255).next();
            bufferBuilder.vertex(ak, ao, al).texture(0.4999, as).color(t, u, v, 255).next();
            bufferBuilder.vertex(ak, 0.0, al).texture(0.4999, ar).color(t, u, v, 255).next();
            bufferBuilder.vertex(am, 0.0, an).texture(0.0, ar).color(t, u, v, 255).next();
            bufferBuilder.vertex(am, ao, an).texture(0.0, as).color(t, u, v, 255).next();
            double at = 0.0;
            if (guardianEntity.age % 2 == 0) {
                at = 0.5;
            }
            bufferBuilder.vertex(y, ao, z).texture(0.5, at + 0.5).color(t, u, v, 255).next();
            bufferBuilder.vertex(aa, ao, ab).texture(1.0, at + 0.5).color(t, u, v, 255).next();
            bufferBuilder.vertex(ae, ao, af).texture(1.0, at).color(t, u, v, 255).next();
            bufferBuilder.vertex(ac, ao, ad).texture(0.5, at).color(t, u, v, 255).next();
            tessellator.draw();
            GlStateManager.popMatrix();
        }
    }

    @Override
    protected Identifier getTexture(GuardianEntity guardianEntity) {
        return SKIN;
    }
}

