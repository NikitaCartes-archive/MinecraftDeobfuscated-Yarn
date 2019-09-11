/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.feature;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
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

    public void method_4195(T mooshroomEntity, float f, float g, float h, float i, float j, float k, float l) {
        if (((PassiveEntity)mooshroomEntity).isBaby() || ((Entity)mooshroomEntity).isInvisible()) {
            return;
        }
        BlockState blockState = ((MooshroomEntity)mooshroomEntity).getMooshroomType().getMushroomState();
        this.bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
        RenderSystem.enableCull();
        RenderSystem.cullFace(GlStateManager.FaceSides.FRONT);
        RenderSystem.pushMatrix();
        RenderSystem.scalef(1.0f, -1.0f, 1.0f);
        RenderSystem.translatef(0.2f, 0.35f, 0.5f);
        RenderSystem.rotatef(42.0f, 0.0f, 1.0f, 0.0f);
        BlockRenderManager blockRenderManager = MinecraftClient.getInstance().getBlockRenderManager();
        RenderSystem.pushMatrix();
        RenderSystem.translatef(-0.5f, -0.5f, 0.5f);
        blockRenderManager.renderDynamic(blockState, 1.0f);
        RenderSystem.popMatrix();
        RenderSystem.pushMatrix();
        RenderSystem.translatef(0.1f, 0.0f, -0.6f);
        RenderSystem.rotatef(42.0f, 0.0f, 1.0f, 0.0f);
        RenderSystem.translatef(-0.5f, -0.5f, 0.5f);
        blockRenderManager.renderDynamic(blockState, 1.0f);
        RenderSystem.popMatrix();
        RenderSystem.popMatrix();
        RenderSystem.pushMatrix();
        ((CowEntityModel)this.getModel()).getHead().applyTransform(0.0625f);
        RenderSystem.scalef(1.0f, -1.0f, 1.0f);
        RenderSystem.translatef(0.0f, 0.7f, -0.2f);
        RenderSystem.rotatef(12.0f, 0.0f, 1.0f, 0.0f);
        RenderSystem.translatef(-0.5f, -0.5f, 0.5f);
        blockRenderManager.renderDynamic(blockState, 1.0f);
        RenderSystem.popMatrix();
        RenderSystem.cullFace(GlStateManager.FaceSides.BACK);
        RenderSystem.disableCull();
    }

    @Override
    public boolean hasHurtOverlay() {
        return true;
    }
}

