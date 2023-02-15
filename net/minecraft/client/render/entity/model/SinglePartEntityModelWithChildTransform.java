/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.model;

import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public abstract class SinglePartEntityModelWithChildTransform<E extends Entity>
extends SinglePartEntityModel<E> {
    private final float childScale;
    private final float childTranslation;

    public SinglePartEntityModelWithChildTransform(float childScale, float childTranslation) {
        this(childScale, childTranslation, RenderLayer::getEntityCutoutNoCull);
    }

    public SinglePartEntityModelWithChildTransform(float childScale, float childTranslation, Function<Identifier, RenderLayer> layerFactory) {
        super(layerFactory);
        this.childTranslation = childTranslation;
        this.childScale = childScale;
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        if (this.child) {
            matrices.push();
            matrices.scale(this.childScale, this.childScale, this.childScale);
            matrices.translate(0.0f, this.childTranslation / 16.0f, 0.0f);
            this.getPart().render(matrices, vertices, light, overlay, red, green, blue, alpha);
            matrices.pop();
        } else {
            this.getPart().render(matrices, vertices, light, overlay, red, green, blue, alpha);
        }
    }
}

