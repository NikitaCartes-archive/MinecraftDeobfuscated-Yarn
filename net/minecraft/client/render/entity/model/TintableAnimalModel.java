/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.AnimalModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;

@Environment(value=EnvType.CLIENT)
public abstract class TintableAnimalModel<E extends Entity>
extends AnimalModel<E> {
    private float redMultiplier = 1.0f;
    private float greenMultiplier = 1.0f;
    private float blueMultiplier = 1.0f;

    public void setColorMultiplier(float f, float g, float h) {
        this.redMultiplier = f;
        this.greenMultiplier = g;
        this.blueMultiplier = h;
    }

    @Override
    public void render(MatrixStack matrixStack, VertexConsumer vertexConsumer, int i, int j, float f, float g, float h, float k) {
        super.render(matrixStack, vertexConsumer, i, j, this.redMultiplier * f, this.greenMultiplier * g, this.blueMultiplier * h, k);
    }
}

