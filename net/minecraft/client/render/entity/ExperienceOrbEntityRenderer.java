/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class ExperienceOrbEntityRenderer
extends EntityRenderer<ExperienceOrbEntity> {
    private static final Identifier SKIN = new Identifier("textures/entity/experience_orb.png");

    public ExperienceOrbEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher);
        this.field_4673 = 0.15f;
        this.field_4672 = 0.75f;
    }

    @Override
    public void render(ExperienceOrbEntity experienceOrbEntity, double d, double e, double f, float g, float h) {
        if (this.renderOutlines || MinecraftClient.getInstance().getEntityRenderManager().gameOptions == null) {
            return;
        }
        GlStateManager.pushMatrix();
        GlStateManager.translatef((float)d, (float)e, (float)f);
        this.bindEntityTexture(experienceOrbEntity);
        DiffuseLighting.enable();
        int i = experienceOrbEntity.getOrbSize();
        float j = (float)(i % 4 * 16 + 0) / 64.0f;
        float k = (float)(i % 4 * 16 + 16) / 64.0f;
        float l = (float)(i / 4 * 16 + 0) / 64.0f;
        float m = (float)(i / 4 * 16 + 16) / 64.0f;
        float n = 1.0f;
        float o = 0.5f;
        float p = 0.25f;
        int q = experienceOrbEntity.getLightmapCoordinates();
        int r = q % 65536;
        int s = q / 65536;
        GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, r, s);
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        float t = 255.0f;
        float u = ((float)experienceOrbEntity.renderTicks + h) / 2.0f;
        int v = (int)((MathHelper.sin(u + 0.0f) + 1.0f) * 0.5f * 255.0f);
        int w = 255;
        int x = (int)((MathHelper.sin(u + 4.1887903f) + 1.0f) * 0.1f * 255.0f);
        GlStateManager.translatef(0.0f, 0.1f, 0.0f);
        GlStateManager.rotatef(180.0f - this.renderManager.cameraYaw, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotatef((float)(this.renderManager.gameOptions.perspective == 2 ? -1 : 1) * -this.renderManager.cameraPitch, 1.0f, 0.0f, 0.0f);
        float y = 0.3f;
        GlStateManager.scalef(0.3f, 0.3f, 0.3f);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE_COLOR_NORMAL);
        bufferBuilder.vertex(-0.5, -0.25, 0.0).texture(j, m).color(v, 255, x, 128).normal(0.0f, 1.0f, 0.0f).next();
        bufferBuilder.vertex(0.5, -0.25, 0.0).texture(k, m).color(v, 255, x, 128).normal(0.0f, 1.0f, 0.0f).next();
        bufferBuilder.vertex(0.5, 0.75, 0.0).texture(k, l).color(v, 255, x, 128).normal(0.0f, 1.0f, 0.0f).next();
        bufferBuilder.vertex(-0.5, 0.75, 0.0).texture(j, l).color(v, 255, x, 128).normal(0.0f, 1.0f, 0.0f).next();
        tessellator.draw();
        GlStateManager.disableBlend();
        GlStateManager.disableRescaleNormal();
        GlStateManager.popMatrix();
        super.render(experienceOrbEntity, d, e, f, g, h);
    }

    @Override
    protected Identifier getTexture(ExperienceOrbEntity experienceOrbEntity) {
        return SKIN;
    }
}

