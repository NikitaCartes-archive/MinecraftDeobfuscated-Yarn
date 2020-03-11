/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.SkullEntityModel;
import net.minecraft.client.util.math.MatrixStack;

@Environment(value=EnvType.CLIENT)
public class DragonHeadEntityModel
extends SkullEntityModel {
    private final ModelPart head;
    private final ModelPart jaw;

    public DragonHeadEntityModel(float scale) {
        this.textureWidth = 256;
        this.textureHeight = 256;
        float f = -16.0f;
        this.head = new ModelPart(this);
        this.head.addCuboid("upperlip", -6.0f, -1.0f, -24.0f, 12, 5, 16, scale, 176, 44);
        this.head.addCuboid("upperhead", -8.0f, -8.0f, -10.0f, 16, 16, 16, scale, 112, 30);
        this.head.mirror = true;
        this.head.addCuboid("scale", -5.0f, -12.0f, -4.0f, 2, 4, 6, scale, 0, 0);
        this.head.addCuboid("nostril", -5.0f, -3.0f, -22.0f, 2, 2, 4, scale, 112, 0);
        this.head.mirror = false;
        this.head.addCuboid("scale", 3.0f, -12.0f, -4.0f, 2, 4, 6, scale, 0, 0);
        this.head.addCuboid("nostril", 3.0f, -3.0f, -22.0f, 2, 2, 4, scale, 112, 0);
        this.jaw = new ModelPart(this);
        this.jaw.setPivot(0.0f, 4.0f, -8.0f);
        this.jaw.addCuboid("jaw", -6.0f, 0.0f, -16.0f, 12, 4, 16, scale, 176, 65);
        this.head.addChild(this.jaw);
    }

    @Override
    public void render(float f, float g, float h) {
        this.jaw.pitch = (float)(Math.sin(f * (float)Math.PI * 0.2f) + 1.0) * 0.2f;
        this.head.yaw = g * ((float)Math.PI / 180);
        this.head.pitch = h * ((float)Math.PI / 180);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
        matrices.push();
        matrices.translate(0.0, -0.374375f, 0.0);
        matrices.scale(0.75f, 0.75f, 0.75f);
        this.head.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
        matrices.pop();
    }
}

