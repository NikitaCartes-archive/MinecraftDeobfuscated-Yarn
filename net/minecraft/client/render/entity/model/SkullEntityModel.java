/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;

@Environment(value=EnvType.CLIENT)
public class SkullEntityModel
extends Model {
    protected final ModelPart skull;

    public SkullEntityModel() {
        this(0, 35, 64, 64);
    }

    public SkullEntityModel(int textureU, int textureV, int textureWidth, int textureHeight) {
        super(RenderLayer::getEntityTranslucent);
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
        this.skull = new ModelPart(this, textureU, textureV);
        this.skull.addCuboid(-4.0f, -8.0f, -4.0f, 8.0f, 8.0f, 8.0f, 0.0f);
        this.skull.setPivot(0.0f, 0.0f, 0.0f);
    }

    public void render(float f, float g, float h) {
        this.skull.yaw = g * ((float)Math.PI / 180);
        this.skull.pitch = h * ((float)Math.PI / 180);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
        this.skull.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
    }
}

