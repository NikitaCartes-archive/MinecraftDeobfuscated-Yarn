/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.feature;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class Deadmau5FeatureRenderer
extends FeatureRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> {
    public Deadmau5FeatureRenderer(FeatureRendererContext<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> featureRendererContext) {
        super(featureRendererContext);
    }

    public void method_4181(AbstractClientPlayerEntity abstractClientPlayerEntity, float f, float g, float h, float i, float j, float k, float l) {
        if (!"deadmau5".equals(abstractClientPlayerEntity.getName().getString()) || !abstractClientPlayerEntity.hasSkinTexture() || abstractClientPlayerEntity.isInvisible()) {
            return;
        }
        this.bindTexture(abstractClientPlayerEntity.getSkinTexture());
        for (int m = 0; m < 2; ++m) {
            float n = MathHelper.lerp(h, abstractClientPlayerEntity.prevYaw, abstractClientPlayerEntity.yaw) - MathHelper.lerp(h, abstractClientPlayerEntity.prevBodyYaw, abstractClientPlayerEntity.bodyYaw);
            float o = MathHelper.lerp(h, abstractClientPlayerEntity.prevPitch, abstractClientPlayerEntity.pitch);
            RenderSystem.pushMatrix();
            RenderSystem.rotatef(n, 0.0f, 1.0f, 0.0f);
            RenderSystem.rotatef(o, 1.0f, 0.0f, 0.0f);
            RenderSystem.translatef(0.375f * (float)(m * 2 - 1), 0.0f, 0.0f);
            RenderSystem.translatef(0.0f, -0.375f, 0.0f);
            RenderSystem.rotatef(-o, 1.0f, 0.0f, 0.0f);
            RenderSystem.rotatef(-n, 0.0f, 1.0f, 0.0f);
            float p = 1.3333334f;
            RenderSystem.scalef(1.3333334f, 1.3333334f, 1.3333334f);
            ((PlayerEntityModel)this.getModel()).renderEars(0.0625f);
            RenderSystem.popMatrix();
        }
    }

    @Override
    public boolean hasHurtOverlay() {
        return true;
    }
}

