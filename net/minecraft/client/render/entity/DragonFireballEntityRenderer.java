/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.entity.projectile.DragonFireballEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class DragonFireballEntityRenderer
extends EntityRenderer<DragonFireballEntity> {
    private static final Identifier SKIN = new Identifier("textures/entity/enderdragon/dragon_fireball.png");

    public DragonFireballEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher);
    }

    @Override
    public void render(DragonFireballEntity dragonFireballEntity, double d, double e, double f, float g, float h) {
        GlStateManager.pushMatrix();
        this.bindEntityTexture(dragonFireballEntity);
        GlStateManager.translatef((float)d, (float)e, (float)f);
        GlStateManager.enableRescaleNormal();
        GlStateManager.scalef(2.0f, 2.0f, 2.0f);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        float i = 1.0f;
        float j = 0.5f;
        float k = 0.25f;
        GlStateManager.rotatef(180.0f - this.renderManager.cameraYaw, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotatef((float)(this.renderManager.gameOptions.perspective == 2 ? -1 : 1) * -this.renderManager.cameraPitch, 1.0f, 0.0f, 0.0f);
        if (this.renderOutlines) {
            GlStateManager.enableColorMaterial();
            GlStateManager.setupSolidRenderingTextureCombine(this.getOutlineColor(dragonFireballEntity));
        }
        bufferBuilder.begin(7, VertexFormats.POSITION_UV_NORMAL);
        bufferBuilder.vertex(-0.5, -0.25, 0.0).texture(0.0, 1.0).normal(0.0f, 1.0f, 0.0f).next();
        bufferBuilder.vertex(0.5, -0.25, 0.0).texture(1.0, 1.0).normal(0.0f, 1.0f, 0.0f).next();
        bufferBuilder.vertex(0.5, 0.75, 0.0).texture(1.0, 0.0).normal(0.0f, 1.0f, 0.0f).next();
        bufferBuilder.vertex(-0.5, 0.75, 0.0).texture(0.0, 0.0).normal(0.0f, 1.0f, 0.0f).next();
        tessellator.draw();
        if (this.renderOutlines) {
            GlStateManager.tearDownSolidRenderingTextureCombine();
            GlStateManager.disableColorMaterial();
        }
        GlStateManager.disableRescaleNormal();
        GlStateManager.popMatrix();
        super.render(dragonFireballEntity, d, e, f, g, h);
    }

    @Override
    protected Identifier getTexture(DragonFireballEntity dragonFireballEntity) {
        return SKIN;
    }
}

