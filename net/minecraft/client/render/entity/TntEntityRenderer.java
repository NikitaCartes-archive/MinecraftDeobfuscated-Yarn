/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.entity.TntEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class TntEntityRenderer
extends EntityRenderer<TntEntity> {
    public TntEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher);
        this.field_4673 = 0.5f;
    }

    public void method_4135(TntEntity tntEntity, double d, double e, double f, float g, float h) {
        float i;
        BlockRenderManager blockRenderManager = MinecraftClient.getInstance().getBlockRenderManager();
        GlStateManager.pushMatrix();
        GlStateManager.translatef((float)d, (float)e + 0.5f, (float)f);
        if ((float)tntEntity.getFuseTimer() - h + 1.0f < 10.0f) {
            i = 1.0f - ((float)tntEntity.getFuseTimer() - h + 1.0f) / 10.0f;
            i = MathHelper.clamp(i, 0.0f, 1.0f);
            i *= i;
            i *= i;
            float j = 1.0f + i * 0.3f;
            GlStateManager.scalef(j, j, j);
        }
        i = (1.0f - ((float)tntEntity.getFuseTimer() - h + 1.0f) / 100.0f) * 0.8f;
        this.bindEntityTexture(tntEntity);
        GlStateManager.rotatef(-90.0f, 0.0f, 1.0f, 0.0f);
        GlStateManager.translatef(-0.5f, -0.5f, 0.5f);
        blockRenderManager.renderDynamic(Blocks.TNT.getDefaultState(), tntEntity.getBrightnessAtEyes());
        GlStateManager.translatef(0.0f, 0.0f, 1.0f);
        if (this.renderOutlines) {
            GlStateManager.enableColorMaterial();
            GlStateManager.setupSolidRenderingTextureCombine(this.getOutlineColor(tntEntity));
            blockRenderManager.renderDynamic(Blocks.TNT.getDefaultState(), 1.0f);
            GlStateManager.tearDownSolidRenderingTextureCombine();
            GlStateManager.disableColorMaterial();
        } else if (tntEntity.getFuseTimer() / 5 % 2 == 0) {
            GlStateManager.disableTexture();
            GlStateManager.disableLighting();
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.DST_ALPHA);
            GlStateManager.color4f(1.0f, 1.0f, 1.0f, i);
            GlStateManager.polygonOffset(-3.0f, -3.0f);
            GlStateManager.enablePolygonOffset();
            blockRenderManager.renderDynamic(Blocks.TNT.getDefaultState(), 1.0f);
            GlStateManager.polygonOffset(0.0f, 0.0f);
            GlStateManager.disablePolygonOffset();
            GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
            GlStateManager.disableBlend();
            GlStateManager.enableLighting();
            GlStateManager.enableTexture();
        }
        GlStateManager.popMatrix();
        super.render(tntEntity, d, e, f, g, h);
    }

    protected Identifier method_4136(TntEntity tntEntity) {
        return SpriteAtlasTexture.BLOCK_ATLAS_TEX;
    }
}

