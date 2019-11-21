/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public abstract class FeatureRenderer<T extends Entity, M extends EntityModel<T>> {
    private final FeatureRendererContext<T, M> context;

    public FeatureRenderer(FeatureRendererContext<T, M> featureRendererContext) {
        this.context = featureRendererContext;
    }

    protected static <T extends LivingEntity> void render(EntityModel<T> entityModel, EntityModel<T> entityModel2, Identifier identifier, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, T livingEntity, float f, float g, float h, float j, float k, float l, float m, float n, float o) {
        if (!livingEntity.isInvisible()) {
            entityModel.copyStateTo(entityModel2);
            entityModel2.animateModel(livingEntity, f, g, l);
            entityModel2.setAngles(livingEntity, f, g, h, j, k);
            FeatureRenderer.renderModel(entityModel2, identifier, matrixStack, vertexConsumerProvider, i, livingEntity, m, n, o);
        }
    }

    protected static <T extends LivingEntity> void renderModel(EntityModel<T> entityModel, Identifier identifier, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, T livingEntity, float f, float g, float h) {
        VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayer.getEntityCutoutNoCull(identifier));
        entityModel.render(matrixStack, vertexConsumer, i, LivingEntityRenderer.method_23622(livingEntity, 0.0f), f, g, h, 1.0f);
    }

    public M getModel() {
        return this.context.getModel();
    }

    protected Identifier getTexture(T entity) {
        return this.context.getTexture(entity);
    }

    public abstract void render(MatrixStack var1, VertexConsumerProvider var2, int var3, T var4, float var5, float var6, float var7, float var8, float var9, float var10);
}

