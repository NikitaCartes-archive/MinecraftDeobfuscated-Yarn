/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.feature;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.GuiLighting;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.StickingOutThingsFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class StingerFeatureRenderer<T extends LivingEntity, M extends EntityModel<T>>
extends StickingOutThingsFeatureRenderer<T, M> {
    private static final Identifier field_20529 = new Identifier("textures/entity/bee/bee_stinger.png");

    public StingerFeatureRenderer(LivingEntityRenderer<T, M> livingEntityRenderer) {
        super(livingEntityRenderer);
    }

    @Override
    protected int getThingCount(T livingEntity) {
        return ((LivingEntity)livingEntity).getStingerCount();
    }

    @Override
    protected void beforeRendering(T livingEntity) {
        GuiLighting.disable();
        RenderSystem.pushMatrix();
        this.bindTexture(field_20529);
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.disableLighting();
        RenderSystem.enableRescaleNormal();
    }

    @Override
    protected void afterRendering() {
        RenderSystem.disableRescaleNormal();
        RenderSystem.enableLighting();
        RenderSystem.popMatrix();
        GuiLighting.enable();
    }

    @Override
    protected void renderThing(Entity entity, float f, float g, float h, float i) {
        RenderSystem.pushMatrix();
        float j = MathHelper.sqrt(f * f + h * h);
        float k = (float)(Math.atan2(f, h) * 57.2957763671875);
        float l = (float)(Math.atan2(g, j) * 57.2957763671875);
        RenderSystem.translatef(0.0f, 0.0f, 0.0f);
        RenderSystem.rotatef(k - 90.0f, 0.0f, 1.0f, 0.0f);
        RenderSystem.rotatef(l, 0.0f, 0.0f, 1.0f);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBufferBuilder();
        float m = 0.0f;
        float n = 0.125f;
        float o = 0.0f;
        float p = 0.0625f;
        float q = 0.03125f;
        RenderSystem.rotatef(45.0f, 1.0f, 0.0f, 0.0f);
        RenderSystem.scalef(0.03125f, 0.03125f, 0.03125f);
        RenderSystem.translatef(2.5f, 0.0f, 0.0f);
        for (int r = 0; r < 4; ++r) {
            RenderSystem.rotatef(90.0f, 1.0f, 0.0f, 0.0f);
            bufferBuilder.begin(7, VertexFormats.POSITION_UV);
            bufferBuilder.vertex(-4.5, -1.0, 0.0).texture(0.0, 0.0).next();
            bufferBuilder.vertex(4.5, -1.0, 0.0).texture(0.125, 0.0).next();
            bufferBuilder.vertex(4.5, 1.0, 0.0).texture(0.125, 0.0625).next();
            bufferBuilder.vertex(-4.5, 1.0, 0.0).texture(0.0, 0.0625).next();
            tessellator.draw();
        }
        RenderSystem.popMatrix();
    }

    @Override
    public boolean hasHurtOverlay() {
        return false;
    }
}

