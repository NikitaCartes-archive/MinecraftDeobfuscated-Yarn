/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.feature;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.IronGolemEntityModel;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.entity.passive.IronGolemEntity;

@Environment(value=EnvType.CLIENT)
public class IronGolemFlowerFeatureRenderer
extends FeatureRenderer<IronGolemEntity, IronGolemEntityModel<IronGolemEntity>> {
    public IronGolemFlowerFeatureRenderer(FeatureRendererContext<IronGolemEntity, IronGolemEntityModel<IronGolemEntity>> featureRendererContext) {
        super(featureRendererContext);
    }

    public void method_4188(IronGolemEntity ironGolemEntity, float f, float g, float h, float i, float j, float k, float l) {
        if (ironGolemEntity.method_6502() == 0) {
            return;
        }
        RenderSystem.enableRescaleNormal();
        RenderSystem.pushMatrix();
        RenderSystem.rotatef(5.0f + 180.0f * ((IronGolemEntityModel)this.getModel()).method_2809().pitch / (float)Math.PI, 1.0f, 0.0f, 0.0f);
        RenderSystem.rotatef(90.0f, 1.0f, 0.0f, 0.0f);
        RenderSystem.translatef(-0.9375f, -0.625f, -0.9375f);
        float m = 0.5f;
        RenderSystem.scalef(0.5f, -0.5f, 0.5f);
        int n = ironGolemEntity.getLightmapCoordinates();
        int o = n % 65536;
        int p = n / 65536;
        RenderSystem.glMultiTexCoord2f(33985, o, p);
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
        MinecraftClient.getInstance().getBlockRenderManager().renderDynamic(Blocks.POPPY.getDefaultState(), 1.0f);
        RenderSystem.popMatrix();
        RenderSystem.disableRescaleNormal();
    }

    @Override
    public boolean hasHurtOverlay() {
        return false;
    }
}

