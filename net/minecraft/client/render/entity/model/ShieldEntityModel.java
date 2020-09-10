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
public class ShieldEntityModel
extends Model {
    private final ModelPart plate;
    private final ModelPart handle;

    public ShieldEntityModel() {
        super(RenderLayer::getEntitySolid);
        this.textureWidth = 64;
        this.textureHeight = 64;
        this.plate = new ModelPart(this, 0, 0);
        this.plate.addCuboid(-6.0f, -11.0f, -2.0f, 12.0f, 22.0f, 1.0f, 0.0f);
        this.handle = new ModelPart(this, 26, 0);
        this.handle.addCuboid(-1.0f, -3.0f, -1.0f, 2.0f, 6.0f, 6.0f, 0.0f);
    }

    public ModelPart getPlate() {
        return this.plate;
    }

    public ModelPart getHandle() {
        return this.handle;
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        this.plate.render(matrices, vertices, light, overlay, red, green, blue, alpha);
        this.handle.render(matrices, vertices, light, overlay, red, green, blue, alpha);
    }
}

