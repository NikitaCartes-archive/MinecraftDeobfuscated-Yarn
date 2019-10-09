/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.SkullEntityModel;
import net.minecraft.util.math.MatrixStack;

@Environment(value=EnvType.CLIENT)
public class SkullOverlayEntityModel
extends SkullEntityModel {
    private final ModelPart field_3377 = new ModelPart(this, 32, 0);

    public SkullOverlayEntityModel() {
        super(0, 0, 64, 64);
        this.field_3377.addCuboid(-4.0f, -8.0f, -4.0f, 8.0f, 8.0f, 8.0f, 0.25f);
        this.field_3377.setPivot(0.0f, 0.0f, 0.0f);
    }

    @Override
    public void render(float f, float g, float h) {
        super.render(f, g, h);
        this.field_3377.yaw = this.skull.yaw;
        this.field_3377.pitch = this.skull.pitch;
    }

    @Override
    public void renderItem(MatrixStack matrixStack, VertexConsumer vertexConsumer, int i, int j, float f, float g, float h) {
        super.renderItem(matrixStack, vertexConsumer, i, j, f, g, h);
        this.field_3377.render(matrixStack, vertexConsumer, 0.0625f, i, j, null, f, g, h);
    }
}

