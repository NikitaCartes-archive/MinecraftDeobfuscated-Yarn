/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.feature;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.FoxModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.passive.FoxEntity;
import net.minecraft.item.ItemStack;

@Environment(value=EnvType.CLIENT)
public class FoxHeldItemFeatureRenderer
extends FeatureRenderer<FoxEntity, FoxModel<FoxEntity>> {
    public FoxHeldItemFeatureRenderer(FeatureRendererContext<FoxEntity, FoxModel<FoxEntity>> featureRendererContext) {
        super(featureRendererContext);
    }

    public void method_18335(FoxEntity foxEntity, float f, float g, float h, float i, float j, float k, float l) {
        float m;
        ItemStack itemStack = foxEntity.getEquippedStack(EquipmentSlot.MAINHAND);
        if (itemStack.isEmpty()) {
            return;
        }
        boolean bl = foxEntity.isSleeping();
        boolean bl2 = foxEntity.isBaby();
        GlStateManager.pushMatrix();
        if (bl2) {
            m = 0.75f;
            GlStateManager.scalef(0.75f, 0.75f, 0.75f);
            GlStateManager.translatef(0.0f, 8.0f * l, 3.35f * l);
        }
        GlStateManager.translatef(((FoxModel)this.getModel()).head.rotationPointX / 16.0f, ((FoxModel)this.getModel()).head.rotationPointY / 16.0f, ((FoxModel)this.getModel()).head.rotationPointZ / 16.0f);
        m = foxEntity.getHeadRoll(h) * 57.295776f;
        GlStateManager.rotatef(m, 0.0f, 0.0f, 1.0f);
        GlStateManager.rotatef(j, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotatef(k, 1.0f, 0.0f, 0.0f);
        if (foxEntity.isBaby()) {
            if (bl) {
                GlStateManager.translatef(0.4f, 0.26f, 0.15f);
            } else {
                GlStateManager.translatef(0.06f, 0.26f, -0.5f);
            }
        } else if (bl) {
            GlStateManager.translatef(0.46f, 0.26f, 0.22f);
        } else {
            GlStateManager.translatef(0.06f, 0.27f, -0.5f);
        }
        GlStateManager.rotatef(90.0f, 1.0f, 0.0f, 0.0f);
        if (bl) {
            GlStateManager.rotatef(90.0f, 0.0f, 0.0f, 1.0f);
        }
        MinecraftClient.getInstance().getItemRenderer().renderHeldItem(itemStack, foxEntity, ModelTransformation.Type.GROUND, false);
        GlStateManager.popMatrix();
    }

    @Override
    public boolean hasHurtOverlay() {
        return false;
    }
}

