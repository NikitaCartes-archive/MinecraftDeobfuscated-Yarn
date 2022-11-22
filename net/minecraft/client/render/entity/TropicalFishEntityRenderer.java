/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.feature.TropicalFishColorFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.LargeTropicalFishEntityModel;
import net.minecraft.client.render.entity.model.SmallTropicalFishEntityModel;
import net.minecraft.client.render.entity.model.TintableCompositeModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.TropicalFishEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

@Environment(value=EnvType.CLIENT)
public class TropicalFishEntityRenderer
extends MobEntityRenderer<TropicalFishEntity, TintableCompositeModel<TropicalFishEntity>> {
    private final TintableCompositeModel<TropicalFishEntity> smallModel = (TintableCompositeModel)this.getModel();
    private final TintableCompositeModel<TropicalFishEntity> largeModel;
    private static final Identifier A_TEXTURE = new Identifier("textures/entity/fish/tropical_a.png");
    private static final Identifier B_TEXTURE = new Identifier("textures/entity/fish/tropical_b.png");

    public TropicalFishEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new SmallTropicalFishEntityModel(context.getPart(EntityModelLayers.TROPICAL_FISH_SMALL)), 0.15f);
        this.largeModel = new LargeTropicalFishEntityModel<TropicalFishEntity>(context.getPart(EntityModelLayers.TROPICAL_FISH_LARGE));
        this.addFeature(new TropicalFishColorFeatureRenderer(this, context.getModelLoader()));
    }

    @Override
    public Identifier getTexture(TropicalFishEntity tropicalFishEntity) {
        return switch (tropicalFishEntity.getVariant().getSize()) {
            default -> throw new IncompatibleClassChangeError();
            case TropicalFishEntity.Size.SMALL -> A_TEXTURE;
            case TropicalFishEntity.Size.LARGE -> B_TEXTURE;
        };
    }

    @Override
    public void render(TropicalFishEntity tropicalFishEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        TintableCompositeModel<TropicalFishEntity> tintableCompositeModel;
        this.model = tintableCompositeModel = (switch (tropicalFishEntity.getVariant().getSize()) {
            default -> throw new IncompatibleClassChangeError();
            case TropicalFishEntity.Size.SMALL -> this.smallModel;
            case TropicalFishEntity.Size.LARGE -> this.largeModel;
        });
        float[] fs = tropicalFishEntity.getBaseColorComponents().getColorComponents();
        tintableCompositeModel.setColorMultiplier(fs[0], fs[1], fs[2]);
        super.render(tropicalFishEntity, f, g, matrixStack, vertexConsumerProvider, i);
        tintableCompositeModel.setColorMultiplier(1.0f, 1.0f, 1.0f);
    }

    @Override
    protected void setupTransforms(TropicalFishEntity tropicalFishEntity, MatrixStack matrixStack, float f, float g, float h) {
        super.setupTransforms(tropicalFishEntity, matrixStack, f, g, h);
        float i = 4.3f * MathHelper.sin(0.6f * f);
        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(i));
        if (!tropicalFishEntity.isTouchingWater()) {
            matrixStack.translate(0.2f, 0.1f, 0.0f);
            matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(90.0f));
        }
    }
}

