/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.feature;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class CapeFeatureRenderer
extends FeatureRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> {
    public CapeFeatureRenderer(FeatureRendererContext<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> featureRendererContext) {
        super(featureRendererContext);
    }

    public void method_4177(AbstractClientPlayerEntity abstractClientPlayerEntity, float f, float g, float h, float i, float j, float k, float l) {
        if (!abstractClientPlayerEntity.canRenderCapeTexture() || abstractClientPlayerEntity.isInvisible() || !abstractClientPlayerEntity.isSkinOverlayVisible(PlayerModelPart.CAPE) || abstractClientPlayerEntity.getCapeTexture() == null) {
            return;
        }
        ItemStack itemStack = abstractClientPlayerEntity.getEquippedStack(EquipmentSlot.CHEST);
        if (itemStack.getItem() == Items.ELYTRA) {
            return;
        }
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.bindTexture(abstractClientPlayerEntity.getCapeTexture());
        GlStateManager.pushMatrix();
        GlStateManager.translatef(0.0f, 0.0f, 0.125f);
        double d = MathHelper.lerp((double)h, abstractClientPlayerEntity.field_7524, abstractClientPlayerEntity.field_7500) - MathHelper.lerp((double)h, abstractClientPlayerEntity.prevX, abstractClientPlayerEntity.x);
        double e = MathHelper.lerp((double)h, abstractClientPlayerEntity.field_7502, abstractClientPlayerEntity.field_7521) - MathHelper.lerp((double)h, abstractClientPlayerEntity.prevY, abstractClientPlayerEntity.y);
        double m = MathHelper.lerp((double)h, abstractClientPlayerEntity.field_7522, abstractClientPlayerEntity.field_7499) - MathHelper.lerp((double)h, abstractClientPlayerEntity.prevZ, abstractClientPlayerEntity.z);
        float n = abstractClientPlayerEntity.field_6220 + (abstractClientPlayerEntity.field_6283 - abstractClientPlayerEntity.field_6220);
        double o = MathHelper.sin(n * ((float)Math.PI / 180));
        double p = -MathHelper.cos(n * ((float)Math.PI / 180));
        float q = (float)e * 10.0f;
        q = MathHelper.clamp(q, -6.0f, 32.0f);
        float r = (float)(d * o + m * p) * 100.0f;
        r = MathHelper.clamp(r, 0.0f, 150.0f);
        float s = (float)(d * p - m * o) * 100.0f;
        s = MathHelper.clamp(s, -20.0f, 20.0f);
        if (r < 0.0f) {
            r = 0.0f;
        }
        float t = MathHelper.lerp(h, abstractClientPlayerEntity.field_7505, abstractClientPlayerEntity.field_7483);
        q += MathHelper.sin(MathHelper.lerp(h, abstractClientPlayerEntity.prevHorizontalSpeed, abstractClientPlayerEntity.horizontalSpeed) * 6.0f) * 32.0f * t;
        if (abstractClientPlayerEntity.isInSneakingPose()) {
            q += 25.0f;
        }
        GlStateManager.rotatef(6.0f + r / 2.0f + q, 1.0f, 0.0f, 0.0f);
        GlStateManager.rotatef(s / 2.0f, 0.0f, 0.0f, 1.0f);
        GlStateManager.rotatef(-s / 2.0f, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotatef(180.0f, 0.0f, 1.0f, 0.0f);
        ((PlayerEntityModel)this.getModel()).renderCape(0.0625f);
        GlStateManager.popMatrix();
    }

    @Override
    public boolean hasHurtOverlay() {
        return false;
    }
}

