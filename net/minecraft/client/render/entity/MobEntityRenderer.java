/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.VisibleRegion;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
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

    public boolean method_4068(T mobEntity, VisibleRegion visibleRegion, double d, double e, double f) {
        if (super.isVisible(mobEntity, visibleRegion, d, e, f)) {
            return true;
        }
        Entity entity = ((MobEntity)mobEntity).getHoldingEntity();
        if (entity != null) {
            return visibleRegion.intersects(entity.getVisibilityBoundingBox());
        }
        return false;
    }

    public void method_4072(T mobEntity, double d, double e, double f, float g, float h) {
        super.method_4054(mobEntity, d, e, f, g, h);
        if (!this.field_4674) {
            this.method_4073(mobEntity, d, e, f, g, h);
        }
    }

    protected void method_4073(T mobEntity, double d, double e, double f, float g, float h) {
        float ae;
        float ad;
        float ac;
        float ab;
        int aa;
        Entity entity = ((MobEntity)mobEntity).getHoldingEntity();
        if (entity == null) {
            return;
        }
        e -= (1.6 - (double)((Entity)mobEntity).getHeight()) * 0.5;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBufferBuilder();
        double i = MathHelper.lerp(h * 0.5f, entity.yaw, entity.prevYaw) * ((float)Math.PI / 180);
        double j = MathHelper.lerp(h * 0.5f, entity.pitch, entity.prevPitch) * ((float)Math.PI / 180);
        double k = Math.cos(i);
        double l = Math.sin(i);
        double m = Math.sin(j);
        if (entity instanceof AbstractDecorationEntity) {
            k = 0.0;
            l = 0.0;
            m = -1.0;
        }
        double n = Math.cos(j);
        double o = MathHelper.lerp((double)h, entity.prevX, entity.x) - k * 0.7 - l * 0.5 * n;
        double p = MathHelper.lerp((double)h, entity.prevY + (double)entity.getStandingEyeHeight() * 0.7, entity.y + (double)entity.getStandingEyeHeight() * 0.7) - m * 0.5 - 0.25;
        double q = MathHelper.lerp((double)h, entity.prevZ, entity.z) - l * 0.7 + k * 0.5 * n;
        double r = (double)(MathHelper.lerp(h, ((MobEntity)mobEntity).field_6283, ((MobEntity)mobEntity).field_6220) * ((float)Math.PI / 180)) + 1.5707963267948966;
        k = Math.cos(r) * (double)((Entity)mobEntity).getWidth() * 0.4;
        l = Math.sin(r) * (double)((Entity)mobEntity).getWidth() * 0.4;
        double s = MathHelper.lerp((double)h, ((MobEntity)mobEntity).prevX, ((MobEntity)mobEntity).x) + k;
        double t = MathHelper.lerp((double)h, ((MobEntity)mobEntity).prevY, ((MobEntity)mobEntity).y);
        double u = MathHelper.lerp((double)h, ((MobEntity)mobEntity).prevZ, ((MobEntity)mobEntity).z) + l;
        d += k;
        f += l;
        double v = (float)(o - s);
        double w = (float)(p - t);
        double x = (float)(q - u);
        GlStateManager.disableTexture();
        GlStateManager.disableLighting();
        GlStateManager.disableCull();
        int y = 24;
        double z = 0.025;
        bufferBuilder.begin(5, VertexFormats.POSITION_COLOR);
        for (aa = 0; aa <= 24; ++aa) {
            ab = 0.5f;
            ac = 0.4f;
            ad = 0.3f;
            if (aa % 2 == 0) {
                ab *= 0.7f;
                ac *= 0.7f;
                ad *= 0.7f;
            }
            ae = (float)aa / 24.0f;
            bufferBuilder.vertex(d + v * (double)ae + 0.0, e + w * (double)(ae * ae + ae) * 0.5 + (double)((24.0f - (float)aa) / 18.0f + 0.125f), f + x * (double)ae).color(ab, ac, ad, 1.0f).next();
            bufferBuilder.vertex(d + v * (double)ae + 0.025, e + w * (double)(ae * ae + ae) * 0.5 + (double)((24.0f - (float)aa) / 18.0f + 0.125f) + 0.025, f + x * (double)ae).color(ab, ac, ad, 1.0f).next();
        }
        tessellator.draw();
        bufferBuilder.begin(5, VertexFormats.POSITION_COLOR);
        for (aa = 0; aa <= 24; ++aa) {
            ab = 0.5f;
            ac = 0.4f;
            ad = 0.3f;
            if (aa % 2 == 0) {
                ab *= 0.7f;
                ac *= 0.7f;
                ad *= 0.7f;
            }
            ae = (float)aa / 24.0f;
            bufferBuilder.vertex(d + v * (double)ae + 0.0, e + w * (double)(ae * ae + ae) * 0.5 + (double)((24.0f - (float)aa) / 18.0f + 0.125f) + 0.025, f + x * (double)ae).color(ab, ac, ad, 1.0f).next();
            bufferBuilder.vertex(d + v * (double)ae + 0.025, e + w * (double)(ae * ae + ae) * 0.5 + (double)((24.0f - (float)aa) / 18.0f + 0.125f), f + x * (double)ae + 0.025).color(ab, ac, ad, 1.0f).next();
        }
        tessellator.draw();
        GlStateManager.enableLighting();
        GlStateManager.enableTexture();
        GlStateManager.enableCull();
    }

    @Override
    protected /* synthetic */ boolean method_4055(LivingEntity livingEntity) {
        return this.method_4071((MobEntity)livingEntity);
    }
}

