/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.feature;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.CowEntityModel;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.MooshroomEntity;
import net.minecraft.entity.passive.PassiveEntity;

@Environment(value=EnvType.CLIENT)
public class MooshroomMushroomFeatureRenderer<T extends MooshroomEntity>
extends FeatureRenderer<T, CowEntityModel<T>> {
    public MooshroomMushroomFeatureRenderer(FeatureRendererContext<T, CowEntityModel<T>> featureRendererContext) {
        super(featureRendererContext);
    }

    @Override
    public void render(T mooshroomEntity, float f, float g, float h, float i, float j, float k, float l) {
        if (((PassiveEntity)mooshroomEntity).isBaby() || ((Entity)mooshroomEntity).isInvisible()) {
            return;
        }
        BlockState blockState = ((MooshroomEntity)mooshroomEntity).getMooshroomType().getMushroomState();
        this.bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
        GlStateManager.enableCull();
        GlStateManager.cullFace(GlStateManager.FaceSides.FRONT);
        GlStateManager.pushMatrix();
        GlStateManager.scalef(1.0f, -1.0f, 1.0f);
        GlStateManager.translatef(0.2f, 0.35f, 0.5f);
        GlStateManager.rotatef(42.0f, 0.0f, 1.0f, 0.0f);
        BlockRenderManager blockRenderManager = MinecraftClient.getInstance().getBlockRenderManager();
        GlStateManager.pushMatrix();
        GlStateManager.translatef(-0.5f, -0.5f, 0.5f);
        blockRenderManager.renderDynamic(blockState, 1.0f);
        GlStateManager.popMatrix();
        GlStateManager.pushMatrix();
        GlStateManager.translatef(0.1f, 0.0f, -0.6f);
        GlStateManager.rotatef(42.0f, 0.0f, 1.0f, 0.0f);
        GlStateManager.translatef(-0.5f, -0.5f, 0.5f);
        blockRenderManager.renderDynamic(blockState, 1.0f);
        GlStateManager.popMatrix();
        GlStateManager.popMatrix();
        GlStateManager.pushMatrix();
        ((CowEntityModel)this.getContextModel()).method_2800().applyTransform(0.0625f);
        GlStateManager.scalef(1.0f, -1.0f, 1.0f);
        GlStateManager.translatef(0.0f, 0.7f, -0.2f);
        GlStateManager.rotatef(12.0f, 0.0f, 1.0f, 0.0f);
        GlStateManager.translatef(-0.5f, -0.5f, 0.5f);
        blockRenderManager.renderDynamic(blockState, 1.0f);
        GlStateManager.popMatrix();
        GlStateManager.cullFace(GlStateManager.FaceSides.BACK);
        GlStateManager.disableCull();
    }

    @Override
    public boolean hasHurtOverlay() {
        return true;
    }
}

