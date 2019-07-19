/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.feature;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.ShulkerEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.ShulkerEntityModel;
import net.minecraft.entity.mob.ShulkerEntity;
import net.minecraft.util.DyeColor;

@Environment(value=EnvType.CLIENT)
public class ShulkerHeadFeatureRenderer
extends FeatureRenderer<ShulkerEntity, ShulkerEntityModel<ShulkerEntity>> {
    public ShulkerHeadFeatureRenderer(FeatureRendererContext<ShulkerEntity, ShulkerEntityModel<ShulkerEntity>> featureRendererContext) {
        super(featureRendererContext);
    }

    @Override
    public void render(ShulkerEntity shulkerEntity, float f, float g, float h, float i, float j, float k, float l) {
        GlStateManager.pushMatrix();
        switch (shulkerEntity.getAttachedFace()) {
            case DOWN: {
                break;
            }
            case EAST: {
                GlStateManager.rotatef(90.0f, 0.0f, 0.0f, 1.0f);
                GlStateManager.rotatef(90.0f, 1.0f, 0.0f, 0.0f);
                GlStateManager.translatef(1.0f, -1.0f, 0.0f);
                GlStateManager.rotatef(180.0f, 0.0f, 1.0f, 0.0f);
                break;
            }
            case WEST: {
                GlStateManager.rotatef(-90.0f, 0.0f, 0.0f, 1.0f);
                GlStateManager.rotatef(90.0f, 1.0f, 0.0f, 0.0f);
                GlStateManager.translatef(-1.0f, -1.0f, 0.0f);
                GlStateManager.rotatef(180.0f, 0.0f, 1.0f, 0.0f);
                break;
            }
            case NORTH: {
                GlStateManager.rotatef(90.0f, 1.0f, 0.0f, 0.0f);
                GlStateManager.translatef(0.0f, -1.0f, -1.0f);
                break;
            }
            case SOUTH: {
                GlStateManager.rotatef(180.0f, 0.0f, 0.0f, 1.0f);
                GlStateManager.rotatef(90.0f, 1.0f, 0.0f, 0.0f);
                GlStateManager.translatef(0.0f, -1.0f, 1.0f);
                break;
            }
            case UP: {
                GlStateManager.rotatef(180.0f, 1.0f, 0.0f, 0.0f);
                GlStateManager.translatef(0.0f, -2.0f, 0.0f);
            }
        }
        ModelPart modelPart = ((ShulkerEntityModel)this.getContextModel()).method_2830();
        modelPart.yaw = j * ((float)Math.PI / 180);
        modelPart.pitch = k * ((float)Math.PI / 180);
        DyeColor dyeColor = shulkerEntity.getColor();
        if (dyeColor == null) {
            this.bindTexture(ShulkerEntityRenderer.SKIN);
        } else {
            this.bindTexture(ShulkerEntityRenderer.SKIN_COLOR[dyeColor.getId()]);
        }
        modelPart.render(l);
        GlStateManager.popMatrix();
    }

    @Override
    public boolean hasHurtOverlay() {
        return false;
    }
}

