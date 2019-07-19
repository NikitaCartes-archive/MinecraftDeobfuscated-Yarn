/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.feature;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EndermanEntityModel;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.entity.mob.EndermanEntity;

@Environment(value=EnvType.CLIENT)
public class EndermanBlockFeatureRenderer
extends FeatureRenderer<EndermanEntity, EndermanEntityModel<EndermanEntity>> {
    public EndermanBlockFeatureRenderer(FeatureRendererContext<EndermanEntity, EndermanEntityModel<EndermanEntity>> featureRendererContext) {
        super(featureRendererContext);
    }

    @Override
    public void render(EndermanEntity endermanEntity, float f, float g, float h, float i, float j, float k, float l) {
        BlockState blockState = endermanEntity.getCarriedBlock();
        if (blockState == null) {
            return;
        }
        GlStateManager.enableRescaleNormal();
        GlStateManager.pushMatrix();
        GlStateManager.translatef(0.0f, 0.6875f, -0.75f);
        GlStateManager.rotatef(20.0f, 1.0f, 0.0f, 0.0f);
        GlStateManager.rotatef(45.0f, 0.0f, 1.0f, 0.0f);
        GlStateManager.translatef(0.25f, 0.1875f, 0.25f);
        float m = 0.5f;
        GlStateManager.scalef(-0.5f, -0.5f, 0.5f);
        int n = endermanEntity.getLightmapCoordinates();
        int o = n % 65536;
        int p = n / 65536;
        GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, o, p);
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
        MinecraftClient.getInstance().getBlockRenderManager().renderDynamic(blockState, 1.0f);
        GlStateManager.popMatrix();
        GlStateManager.disableRescaleNormal();
    }

    @Override
    public boolean hasHurtOverlay() {
        return false;
    }
}

