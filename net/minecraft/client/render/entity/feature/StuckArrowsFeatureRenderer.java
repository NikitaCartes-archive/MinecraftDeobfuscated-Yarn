/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.feature;

import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Box;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class StuckArrowsFeatureRenderer<T extends LivingEntity, M extends EntityModel<T>>
extends FeatureRenderer<T, M> {
    private final EntityRenderDispatcher field_17153;

    public StuckArrowsFeatureRenderer(LivingEntityRenderer<T, M> livingEntityRenderer) {
        super(livingEntityRenderer);
        this.field_17153 = livingEntityRenderer.getRenderManager();
    }

    @Override
    public void render(T livingEntity, float f, float g, float h, float i, float j, float k, float l) {
        int m = ((LivingEntity)livingEntity).getStuckArrowCount();
        if (m <= 0) {
            return;
        }
        ArrowEntity entity = new ArrowEntity(((LivingEntity)livingEntity).world, ((LivingEntity)livingEntity).x, ((LivingEntity)livingEntity).y, ((LivingEntity)livingEntity).z);
        Random random = new Random(((Entity)livingEntity).getEntityId());
        DiffuseLighting.disable();
        for (int n = 0; n < m; ++n) {
            GlStateManager.pushMatrix();
            ModelPart modelPart = ((Model)this.getContextModel()).getRandomCuboid(random);
            Box box = modelPart.boxes.get(random.nextInt(modelPart.boxes.size()));
            modelPart.applyTransform(0.0625f);
            float o = random.nextFloat();
            float p = random.nextFloat();
            float q = random.nextFloat();
            float r = MathHelper.lerp(o, box.xMin, box.xMax) / 16.0f;
            float s = MathHelper.lerp(p, box.yMin, box.yMax) / 16.0f;
            float t = MathHelper.lerp(q, box.zMin, box.zMax) / 16.0f;
            GlStateManager.translatef(r, s, t);
            o = o * 2.0f - 1.0f;
            p = p * 2.0f - 1.0f;
            q = q * 2.0f - 1.0f;
            float u = MathHelper.sqrt((o *= -1.0f) * o + (q *= -1.0f) * q);
            entity.yaw = (float)(Math.atan2(o, q) * 57.2957763671875);
            entity.pitch = (float)(Math.atan2(p *= -1.0f, u) * 57.2957763671875);
            entity.prevYaw = entity.yaw;
            entity.prevPitch = entity.pitch;
            double d = 0.0;
            double e = 0.0;
            double v = 0.0;
            this.field_17153.render(entity, 0.0, 0.0, 0.0, 0.0f, h, false);
            GlStateManager.popMatrix();
        }
        DiffuseLighting.enable();
    }

    @Override
    public boolean hasHurtOverlay() {
        return false;
    }
}

