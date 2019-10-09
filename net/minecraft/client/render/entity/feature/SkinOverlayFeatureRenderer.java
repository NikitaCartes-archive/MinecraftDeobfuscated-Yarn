/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.LayeredVertexConsumerStorage;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.SkinOverlayOwner;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MatrixStack;

@Environment(value=EnvType.CLIENT)
public abstract class SkinOverlayFeatureRenderer<T extends Entity, M extends EntityModel<T>>
extends FeatureRenderer<T, M> {
    public SkinOverlayFeatureRenderer(FeatureRendererContext<T, M> featureRendererContext) {
        super(featureRendererContext);
    }

    @Override
    public void render(MatrixStack matrixStack, LayeredVertexConsumerStorage layeredVertexConsumerStorage, int i, T entity, float f, float g, float h, float j, float k, float l, float m) {
        if (!((SkinOverlayOwner)entity).shouldRenderOverlay()) {
            return;
        }
        float n = (float)((Entity)entity).age + h;
        EntityModel<T> entityModel = this.method_23203();
        entityModel.animateModel(entity, f, g, h);
        ((EntityModel)this.getModel()).copyStateTo(entityModel);
        VertexConsumer vertexConsumer = layeredVertexConsumerStorage.getBuffer(RenderLayer.getPowerSwirl(this.method_23201(), this.method_23202(n), n * 0.01f));
        entityModel.setAngles(entity, f, g, j, k, l, m);
        entityModel.renderItem(matrixStack, vertexConsumer, i, OverlayTexture.field_21444, 0.5f, 0.5f, 0.5f);
    }

    protected abstract float method_23202(float var1);

    protected abstract Identifier method_23201();

    protected abstract EntityModel<T> method_23203();
}

