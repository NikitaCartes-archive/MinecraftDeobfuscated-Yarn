/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5599;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.LargeTropicalFishEntityModel;
import net.minecraft.client.render.entity.model.SmallTropicalFishEntityModel;
import net.minecraft.client.render.entity.model.TintableCompositeModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.TropicalFishEntity;

@Environment(value=EnvType.CLIENT)
public class TropicalFishColorFeatureRenderer
extends FeatureRenderer<TropicalFishEntity, TintableCompositeModel<TropicalFishEntity>> {
    private final SmallTropicalFishEntityModel<TropicalFishEntity> smallModel;
    private final LargeTropicalFishEntityModel<TropicalFishEntity> largeModel;

    public TropicalFishColorFeatureRenderer(FeatureRendererContext<TropicalFishEntity, TintableCompositeModel<TropicalFishEntity>> featureRendererContext, class_5599 arg) {
        super(featureRendererContext);
        this.smallModel = new SmallTropicalFishEntityModel(arg.method_32072(EntityModelLayers.TROPICAL_FISH_SMALL_PATTERN));
        this.largeModel = new LargeTropicalFishEntityModel(arg.method_32072(EntityModelLayers.TROPICAL_FISH_LARGE_PATTERN));
    }

    @Override
    public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, TropicalFishEntity tropicalFishEntity, float f, float g, float h, float j, float k, float l) {
        TintableCompositeModel entityModel = tropicalFishEntity.getShape() == 0 ? this.smallModel : this.largeModel;
        float[] fs = tropicalFishEntity.getPatternColorComponents();
        TropicalFishColorFeatureRenderer.render(this.getContextModel(), entityModel, tropicalFishEntity.getVarietyId(), matrixStack, vertexConsumerProvider, i, tropicalFishEntity, f, g, j, k, l, h, fs[0], fs[1], fs[2]);
    }
}

