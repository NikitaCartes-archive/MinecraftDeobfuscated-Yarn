/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.feature.EyesFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.PhantomEntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class PhantomEyesFeatureRenderer<T extends Entity>
extends EyesFeatureRenderer<T, PhantomEntityModel<T>> {
    private static final Identifier SKIN = new Identifier("textures/entity/phantom_eyes.png");

    public PhantomEyesFeatureRenderer(FeatureRendererContext<T, PhantomEntityModel<T>> featureRendererContext) {
        super(featureRendererContext);
    }

    @Override
    public Identifier getEyesTexture() {
        return SKIN;
    }
}

