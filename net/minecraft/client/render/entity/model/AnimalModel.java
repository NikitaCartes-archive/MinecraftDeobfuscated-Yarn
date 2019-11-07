/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.model;

import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public abstract class AnimalModel<E extends Entity>
extends EntityModel<E> {
    private final boolean headScaled;
    private final float childHeadYOffset;
    private final float childHeadZOffset;
    private final float invertedChildHeadScale;
    private final float invertedChildBodyScale;
    private final float childBodyYOffset;

    protected AnimalModel(boolean bl, float f, float g) {
        this(bl, f, g, 2.0f, 2.0f, 24.0f);
    }

    protected AnimalModel(boolean bl, float f, float g, float h, float i, float j) {
        this(RenderLayer::getEntityCutoutNoCull, bl, f, g, h, i, j);
    }

    protected AnimalModel(Function<Identifier, RenderLayer> function, boolean bl, float f, float g, float h, float i, float j) {
        super(function);
        this.headScaled = bl;
        this.childHeadYOffset = f;
        this.childHeadZOffset = g;
        this.invertedChildHeadScale = h;
        this.invertedChildBodyScale = i;
        this.childBodyYOffset = j;
    }

    protected AnimalModel() {
        this(false, 5.0f, 2.0f);
    }

    @Override
    public void render(MatrixStack matrixStack, VertexConsumer vertexConsumer, int i, int j, float f, float g, float h) {
        if (this.isChild) {
            float k;
            matrixStack.push();
            if (this.headScaled) {
                k = 1.5f / this.invertedChildHeadScale;
                matrixStack.scale(k, k, k);
            }
            matrixStack.translate(0.0, this.childHeadYOffset / 16.0f, this.childHeadZOffset / 16.0f);
            this.getHeadParts().forEach(modelPart -> modelPart.render(matrixStack, vertexConsumer, i, j, null, f, g, h));
            matrixStack.pop();
            matrixStack.push();
            k = 1.0f / this.invertedChildBodyScale;
            matrixStack.scale(k, k, k);
            matrixStack.translate(0.0, this.childBodyYOffset / 16.0f, 0.0);
            this.getBodyParts().forEach(modelPart -> modelPart.render(matrixStack, vertexConsumer, i, j, null, f, g, h));
            matrixStack.pop();
        } else {
            this.getHeadParts().forEach(modelPart -> modelPart.render(matrixStack, vertexConsumer, i, j, null, f, g, h));
            this.getBodyParts().forEach(modelPart -> modelPart.render(matrixStack, vertexConsumer, i, j, null, f, g, h));
        }
    }

    protected abstract Iterable<ModelPart> getHeadParts();

    protected abstract Iterable<ModelPart> getBodyParts();
}

