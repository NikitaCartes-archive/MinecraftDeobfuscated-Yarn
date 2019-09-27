/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4587;
import net.minecraft.class_4594;
import net.minecraft.class_4597;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.TropicalFishEntityModelA;
import net.minecraft.client.render.entity.model.TropicalFishEntityModelB;
import net.minecraft.entity.passive.TropicalFishEntity;

@Environment(value=EnvType.CLIENT)
public class TropicalFishSomethingFeatureRenderer
extends FeatureRenderer<TropicalFishEntity, EntityModel<TropicalFishEntity>> {
    private final TropicalFishEntityModelA<TropicalFishEntity> modelA = new TropicalFishEntityModelA(0.008f);
    private final TropicalFishEntityModelB<TropicalFishEntity> modelB = new TropicalFishEntityModelB(0.008f);

    public TropicalFishSomethingFeatureRenderer(FeatureRendererContext<TropicalFishEntity, EntityModel<TropicalFishEntity>> featureRendererContext) {
        super(featureRendererContext);
    }

    public void method_4205(class_4587 arg, class_4597 arg2, int i, TropicalFishEntity tropicalFishEntity, float f, float g, float h, float j, float k, float l, float m) {
        class_4594 entityModel = tropicalFishEntity.getShape() == 0 ? this.modelA : this.modelB;
        float[] fs = tropicalFishEntity.getPatternColorComponents();
        TropicalFishSomethingFeatureRenderer.method_23196(this.getModel(), entityModel, tropicalFishEntity.getVarietyId(), arg, arg2, i, tropicalFishEntity, f, g, j, k, l, m, h, fs[0], fs[1], fs[2]);
    }
}

