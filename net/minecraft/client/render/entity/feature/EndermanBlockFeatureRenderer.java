/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.feature;

import com.mojang.blaze3d.systems.RenderSystem;
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

    public void method_4179(EndermanEntity endermanEntity, float f, float g, float h, float i, float j, float k, float l) {
        BlockState blockState = endermanEntity.getCarriedBlock();
        if (blockState == null) {
            return;
        }
        RenderSystem.enableRescaleNormal();
        RenderSystem.pushMatrix();
        RenderSystem.translatef(0.0f, 0.6875f, -0.75f);
        RenderSystem.rotatef(20.0f, 1.0f, 0.0f, 0.0f);
        RenderSystem.rotatef(45.0f, 0.0f, 1.0f, 0.0f);
        RenderSystem.translatef(0.25f, 0.1875f, 0.25f);
        float m = 0.5f;
        RenderSystem.scalef(-0.5f, -0.5f, 0.5f);
        int n = endermanEntity.getLightmapCoordinates();
        int o = n % 65536;
        int p = n / 65536;
        RenderSystem.glMultiTexCoord2f(33985, o, p);
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
        MinecraftClient.getInstance().getBlockRenderManager().renderDynamic(blockState, 1.0f);
        RenderSystem.popMatrix();
        RenderSystem.disableRescaleNormal();
    }

    @Override
    public boolean hasHurtOverlay() {
        return false;
    }
}

