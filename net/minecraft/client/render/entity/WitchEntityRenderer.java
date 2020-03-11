/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.feature.WitchHeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.WitchEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.WitchEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class WitchEntityRenderer
extends MobEntityRenderer<WitchEntity, WitchEntityModel<WitchEntity>> {
    private static final Identifier TEXTURE = new Identifier("textures/entity/witch.png");

    public WitchEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher, new WitchEntityModel(0.0f), 0.5f);
        this.addFeature(new WitchHeldItemFeatureRenderer<WitchEntity>(this));
    }

    @Override
    public void render(WitchEntity witchEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        ((WitchEntityModel)this.model).setLiftingNose(!witchEntity.getMainHandStack().isEmpty());
        super.render(witchEntity, f, g, matrixStack, vertexConsumerProvider, i);
    }

    @Override
    public Identifier getTexture(WitchEntity witchEntity) {
        return TEXTURE;
    }

    @Override
    protected void scale(WitchEntity witchEntity, MatrixStack matrixStack, float f) {
        float g = 0.9375f;
        matrixStack.scale(0.9375f, 0.9375f, 0.9375f);
    }
}

