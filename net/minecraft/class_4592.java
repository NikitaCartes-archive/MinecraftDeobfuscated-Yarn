/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MatrixStack;

@Environment(value=EnvType.CLIENT)
public abstract class class_4592<E extends Entity>
extends EntityModel<E> {
    private final boolean field_20915;
    private final float field_20916;
    private final float field_20917;
    private final float field_20918;
    private final float field_20919;
    private final float field_20920;

    protected class_4592(boolean bl, float f, float g) {
        this(bl, f, g, 2.0f, 2.0f, 24.0f);
    }

    protected class_4592(boolean bl, float f, float g, float h, float i, float j) {
        this.field_20915 = bl;
        this.field_20916 = f;
        this.field_20917 = g;
        this.field_20918 = h;
        this.field_20919 = i;
        this.field_20920 = j;
    }

    protected class_4592() {
        this(false, 5.0f, 2.0f);
    }

    @Override
    public void method_17116(MatrixStack matrixStack, VertexConsumer vertexConsumer, int i, float f, float g, float h) {
        if (this.isChild) {
            float j;
            matrixStack.push();
            if (this.field_20915) {
                j = 1.5f / this.field_20918;
                matrixStack.scale(j, j, j);
            }
            matrixStack.translate(0.0, this.field_20916 / 16.0f, this.field_20917 / 16.0f);
            this.method_22946().forEach(modelPart -> modelPart.render(matrixStack, vertexConsumer, 0.0625f, i, null, f, g, h));
            matrixStack.pop();
            matrixStack.push();
            j = 1.0f / this.field_20919;
            matrixStack.scale(j, j, j);
            matrixStack.translate(0.0, this.field_20920 / 16.0f, 0.0);
            this.method_22948().forEach(modelPart -> modelPart.render(matrixStack, vertexConsumer, 0.0625f, i, null, f, g, h));
            matrixStack.pop();
        } else {
            this.method_22946().forEach(modelPart -> modelPart.render(matrixStack, vertexConsumer, 0.0625f, i, null, f, g, h));
            this.method_22948().forEach(modelPart -> modelPart.render(matrixStack, vertexConsumer, 0.0625f, i, null, f, g, h));
        }
    }

    protected abstract Iterable<ModelPart> method_22946();

    protected abstract Iterable<ModelPart> method_22948();
}

