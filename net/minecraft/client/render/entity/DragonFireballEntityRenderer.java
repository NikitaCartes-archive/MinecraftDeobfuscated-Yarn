/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity;

import com.mojang.blaze3d.systems.RenderSystem;
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

    public void method_3906(DragonFireballEntity dragonFireballEntity, double d, double e, double f, float g, float h) {
        RenderSystem.pushMatrix();
        this.bindEntityTexture(dragonFireballEntity);
        RenderSystem.translatef((float)d, (float)e, (float)f);
        RenderSystem.enableRescaleNormal();
        RenderSystem.scalef(2.0f, 2.0f, 2.0f);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBufferBuilder();
        float i = 1.0f;
        float j = 0.5f;
        float k = 0.25f;
        RenderSystem.rotatef(180.0f - this.renderManager.cameraYaw, 0.0f, 1.0f, 0.0f);
        RenderSystem.rotatef((float)(this.renderManager.gameOptions.perspective == 2 ? -1 : 1) * -this.renderManager.cameraPitch, 1.0f, 0.0f, 0.0f);
        if (this.renderOutlines) {
            RenderSystem.enableColorMaterial();
            RenderSystem.setupSolidRenderingTextureCombine(this.getOutlineColor(dragonFireballEntity));
        }
        bufferBuilder.begin(7, VertexFormats.POSITION_UV_NORMAL);
        bufferBuilder.vertex(-0.5, -0.25, 0.0).texture(0.0, 1.0).normal(0.0f, 1.0f, 0.0f).next();
        bufferBuilder.vertex(0.5, -0.25, 0.0).texture(1.0, 1.0).normal(0.0f, 1.0f, 0.0f).next();
        bufferBuilder.vertex(0.5, 0.75, 0.0).texture(1.0, 0.0).normal(0.0f, 1.0f, 0.0f).next();
        bufferBuilder.vertex(-0.5, 0.75, 0.0).texture(0.0, 0.0).normal(0.0f, 1.0f, 0.0f).next();
        tessellator.draw();
        if (this.renderOutlines) {
            RenderSystem.tearDownSolidRenderingTextureCombine();
            RenderSystem.disableColorMaterial();
        }
        RenderSystem.disableRescaleNormal();
        RenderSystem.popMatrix();
        super.render(dragonFireballEntity, d, e, f, g, h);
    }

    protected Identifier method_3905(DragonFireballEntity dragonFireballEntity) {
        return SKIN;
    }
}

