/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.feature;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.TropicalFishEntityModelA;
import net.minecraft.client.render.entity.model.TropicalFishEntityModelB;
import net.minecraft.entity.passive.TropicalFishEntity;

@Environment(value=EnvType.CLIENT)
public class TropicalFishSomethingFeatureRenderer
extends FeatureRenderer<TropicalFishEntity, EntityModel<TropicalFishEntity>> {
    private final TropicalFishEntityModelA<TropicalFishEntity> field_17157 = new TropicalFishEntityModelA(0.008f);
    private final TropicalFishEntityModelB<TropicalFishEntity> field_4903 = new TropicalFishEntityModelB(0.008f);

    public TropicalFishSomethingFeatureRenderer(FeatureRendererContext<TropicalFishEntity, EntityModel<TropicalFishEntity>> featureRendererContext) {
        super(featureRendererContext);
    }

    public void method_4205(TropicalFishEntity tropicalFishEntity, float f, float g, float h, float i, float j, float k, float l) {
        if (tropicalFishEntity.isInvisible()) {
            return;
        }
        EntityModel entityModel = tropicalFishEntity.getShape() == 0 ? this.field_17157 : this.field_4903;
        this.bindTexture(tropicalFishEntity.getVarietyId());
        float[] fs = tropicalFishEntity.getPatternColorComponents();
        GlStateManager.color3f(fs[0], fs[1], fs[2]);
        ((EntityModel)this.getModel()).copyStateTo(entityModel);
        entityModel.animateModel((TropicalFishEntity)tropicalFishEntity, f, g, h);
        entityModel.render(tropicalFishEntity, f, g, i, j, k, l);
    }

    @Override
    public boolean hasHurtOverlay() {
        return true;
    }
}

