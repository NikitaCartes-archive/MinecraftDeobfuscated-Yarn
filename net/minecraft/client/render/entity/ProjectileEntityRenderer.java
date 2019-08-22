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
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public abstract class ProjectileEntityRenderer<T extends ProjectileEntity>
extends EntityRenderer<T> {
    public ProjectileEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher);
    }

    public void method_3875(T projectileEntity, double d, double e, double f, float g, float h) {
        this.bindEntityTexture(projectileEntity);
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.pushMatrix();
        RenderSystem.disableLighting();
        RenderSystem.translatef((float)d, (float)e, (float)f);
        RenderSystem.rotatef(MathHelper.lerp(h, ((ProjectileEntity)projectileEntity).prevYaw, ((ProjectileEntity)projectileEntity).yaw) - 90.0f, 0.0f, 1.0f, 0.0f);
        RenderSystem.rotatef(MathHelper.lerp(h, ((ProjectileEntity)projectileEntity).prevPitch, ((ProjectileEntity)projectileEntity).pitch), 0.0f, 0.0f, 1.0f);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBufferBuilder();
        boolean i = false;
        float j = 0.0f;
        float k = 0.5f;
        float l = 0.0f;
        float m = 0.15625f;
        float n = 0.0f;
        float o = 0.15625f;
        float p = 0.15625f;
        float q = 0.3125f;
        float r = 0.05625f;
        RenderSystem.enableRescaleNormal();
        float s = (float)((ProjectileEntity)projectileEntity).shake - h;
        if (s > 0.0f) {
            float t = -MathHelper.sin(s * 3.0f) * s;
            RenderSystem.rotatef(t, 0.0f, 0.0f, 1.0f);
        }
        RenderSystem.rotatef(45.0f, 1.0f, 0.0f, 0.0f);
        RenderSystem.scalef(0.05625f, 0.05625f, 0.05625f);
        RenderSystem.translatef(-4.0f, 0.0f, 0.0f);
        if (this.renderOutlines) {
            RenderSystem.enableColorMaterial();
            RenderSystem.setupSolidRenderingTextureCombine(this.getOutlineColor(projectileEntity));
        }
        RenderSystem.normal3f(0.05625f, 0.0f, 0.0f);
        bufferBuilder.begin(7, VertexFormats.POSITION_UV);
        bufferBuilder.vertex(-7.0, -2.0, -2.0).texture(0.0, 0.15625).next();
        bufferBuilder.vertex(-7.0, -2.0, 2.0).texture(0.15625, 0.15625).next();
        bufferBuilder.vertex(-7.0, 2.0, 2.0).texture(0.15625, 0.3125).next();
        bufferBuilder.vertex(-7.0, 2.0, -2.0).texture(0.0, 0.3125).next();
        tessellator.draw();
        RenderSystem.normal3f(-0.05625f, 0.0f, 0.0f);
        bufferBuilder.begin(7, VertexFormats.POSITION_UV);
        bufferBuilder.vertex(-7.0, 2.0, -2.0).texture(0.0, 0.15625).next();
        bufferBuilder.vertex(-7.0, 2.0, 2.0).texture(0.15625, 0.15625).next();
        bufferBuilder.vertex(-7.0, -2.0, 2.0).texture(0.15625, 0.3125).next();
        bufferBuilder.vertex(-7.0, -2.0, -2.0).texture(0.0, 0.3125).next();
        tessellator.draw();
        for (int u = 0; u < 4; ++u) {
            RenderSystem.rotatef(90.0f, 1.0f, 0.0f, 0.0f);
            RenderSystem.normal3f(0.0f, 0.0f, 0.05625f);
            bufferBuilder.begin(7, VertexFormats.POSITION_UV);
            bufferBuilder.vertex(-8.0, -2.0, 0.0).texture(0.0, 0.0).next();
            bufferBuilder.vertex(8.0, -2.0, 0.0).texture(0.5, 0.0).next();
            bufferBuilder.vertex(8.0, 2.0, 0.0).texture(0.5, 0.15625).next();
            bufferBuilder.vertex(-8.0, 2.0, 0.0).texture(0.0, 0.15625).next();
            tessellator.draw();
        }
        if (this.renderOutlines) {
            RenderSystem.tearDownSolidRenderingTextureCombine();
            RenderSystem.disableColorMaterial();
        }
        RenderSystem.disableRescaleNormal();
        RenderSystem.enableLighting();
        RenderSystem.popMatrix();
        super.render(projectileEntity, d, e, f, g, h);
    }
}

