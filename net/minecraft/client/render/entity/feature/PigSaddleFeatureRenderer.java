/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.PigEntityModel;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class PigSaddleFeatureRenderer
extends FeatureRenderer<PigEntity, PigEntityModel<PigEntity>> {
    private static final Identifier SKIN = new Identifier("textures/entity/pig/pig_saddle.png");
    private final PigEntityModel<PigEntity> model = new PigEntityModel(0.5f);

    public PigSaddleFeatureRenderer(FeatureRendererContext<PigEntity, PigEntityModel<PigEntity>> featureRendererContext) {
        super(featureRendererContext);
    }

    public void method_4196(PigEntity pigEntity, float f, float g, float h, float i, float j, float k, float l) {
        if (!pigEntity.isSaddled()) {
            return;
        }
        this.bindTexture(SKIN);
        ((PigEntityModel)this.getModel()).copyStateTo(this.model);
        this.model.render(pigEntity, f, g, i, j, k, l);
    }

    @Override
    public boolean hasHurtOverlay() {
        return false;
    }
}

