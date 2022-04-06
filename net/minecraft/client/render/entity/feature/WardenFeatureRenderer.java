/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.feature;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.WardenEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.WardenEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class WardenFeatureRenderer<T extends WardenEntity, M extends WardenEntityModel<T>>
extends FeatureRenderer<T, M> {
    private final Identifier texture;
    private final AnimationAngleAdjuster<T> animationAngleAdjuster;
    private final ModelPartVisibility<T, M> modelPartVisibility;

    public WardenFeatureRenderer(FeatureRendererContext<T, M> context, Identifier texture, AnimationAngleAdjuster<T> animationAngleAdjuster, ModelPartVisibility<T, M> modelPartVisibility) {
        super(context);
        this.texture = texture;
        this.animationAngleAdjuster = animationAngleAdjuster;
        this.modelPartVisibility = modelPartVisibility;
    }

    @Override
    public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, T wardenEntity, float f, float g, float h, float j, float k, float l) {
        if (((Entity)wardenEntity).isInvisible()) {
            return;
        }
        this.updateModelPartVisibility();
        VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayer.getEntityTranslucentEmissive(this.texture));
        ((WardenEntityModel)this.getContextModel()).render(matrixStack, vertexConsumer, i, LivingEntityRenderer.getOverlay(wardenEntity, 0.0f), 1.0f, 1.0f, 1.0f, this.animationAngleAdjuster.apply(wardenEntity, h, j));
        this.unhideAllModelParts();
    }

    private void updateModelPartVisibility() {
        List<ModelPart> list = this.modelPartVisibility.getPartsToDraw((WardenEntityModel)this.getContextModel());
        ((WardenEntityModel)this.getContextModel()).getPart().traverse().forEach(part -> {
            part.hidden = true;
        });
        list.forEach(part -> {
            part.hidden = false;
        });
    }

    private void unhideAllModelParts() {
        ((WardenEntityModel)this.getContextModel()).getPart().traverse().forEach(part -> {
            part.hidden = false;
        });
    }

    @Environment(value=EnvType.CLIENT)
    public static interface AnimationAngleAdjuster<T extends WardenEntity> {
        public float apply(T var1, float var2, float var3);
    }

    @Environment(value=EnvType.CLIENT)
    public static interface ModelPartVisibility<T extends WardenEntity, M extends EntityModel<T>> {
        public List<ModelPart> getPartsToDraw(M var1);
    }
}

