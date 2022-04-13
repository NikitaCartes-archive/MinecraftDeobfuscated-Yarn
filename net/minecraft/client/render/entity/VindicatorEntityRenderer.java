/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.IllagerEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.IllagerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.VindicatorEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class VindicatorEntityRenderer
extends IllagerEntityRenderer<VindicatorEntity> {
    private static final Identifier TEXTURE = new Identifier("textures/entity/illager/vindicator.png");

    public VindicatorEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new IllagerEntityModel(context.getPart(EntityModelLayers.VINDICATOR)), 0.5f);
        this.addFeature(new HeldItemFeatureRenderer<VindicatorEntity, IllagerEntityModel<VindicatorEntity>>((FeatureRendererContext)this, context.getHeldItemRenderer()){

            @Override
            public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, VindicatorEntity vindicatorEntity, float f, float g, float h, float j, float k, float l) {
                if (vindicatorEntity.isAttacking()) {
                    super.render(matrixStack, vertexConsumerProvider, i, vindicatorEntity, f, g, h, j, k, l);
                }
            }
        });
    }

    @Override
    public Identifier getTexture(VindicatorEntity vindicatorEntity) {
        return TEXTURE;
    }
}

