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
    private final class_7311<T, M> field_38464;

    public WardenFeatureRenderer(FeatureRendererContext<T, M> context, Identifier texture, AnimationAngleAdjuster<T> animationAngleAdjuster, class_7311<T, M> arg) {
        super(context);
        this.texture = texture;
        this.animationAngleAdjuster = animationAngleAdjuster;
        this.field_38464 = arg;
    }

    @Override
    public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, T wardenEntity, float f, float g, float h, float j, float k, float l) {
        if (((Entity)wardenEntity).isInvisible()) {
            return;
        }
        this.method_42746();
        VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayer.getEntityTranslucentEmissive(this.texture));
        ((WardenEntityModel)this.getContextModel()).render(matrixStack, vertexConsumer, i, LivingEntityRenderer.getOverlay(wardenEntity, 0.0f), 1.0f, 1.0f, 1.0f, this.animationAngleAdjuster.apply(wardenEntity, h, j));
        this.method_42748();
    }

    private void method_42746() {
        List<ModelPart> list = this.field_38464.getPartsToDraw((WardenEntityModel)this.getContextModel());
        ((WardenEntityModel)this.getContextModel()).getPart().traverse().forEach(modelPart -> {
            modelPart.field_38456 = true;
        });
        list.forEach(modelPart -> {
            modelPart.field_38456 = false;
        });
    }

    private void method_42748() {
        ((WardenEntityModel)this.getContextModel()).getPart().traverse().forEach(modelPart -> {
            modelPart.field_38456 = false;
        });
    }

    @Environment(value=EnvType.CLIENT)
    public static interface AnimationAngleAdjuster<T extends WardenEntity> {
        public float apply(T var1, float var2, float var3);
    }

    @Environment(value=EnvType.CLIENT)
    public static interface class_7311<T extends WardenEntity, M extends EntityModel<T>> {
        public List<ModelPart> getPartsToDraw(M var1);
    }
}

