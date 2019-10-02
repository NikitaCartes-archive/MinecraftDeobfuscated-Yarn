/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.SkinOverlayFeatureRenderer;
import net.minecraft.client.render.entity.model.CreeperEntityModel;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class CreeperChargeFeatureRenderer
extends SkinOverlayFeatureRenderer<CreeperEntity, CreeperEntityModel<CreeperEntity>> {
    private static final Identifier SKIN = new Identifier("textures/entity/creeper/creeper_armor.png");
    private final CreeperEntityModel<CreeperEntity> model = new CreeperEntityModel(2.0f);

    public CreeperChargeFeatureRenderer(FeatureRendererContext<CreeperEntity, CreeperEntityModel<CreeperEntity>> featureRendererContext) {
        super(featureRendererContext);
    }

    @Override
    protected float method_23202(float f) {
        return f * 0.01f;
    }

    @Override
    protected Identifier method_23201() {
        return SKIN;
    }

    @Override
    protected EntityModel<CreeperEntity> method_23203() {
        return this.model;
    }
}

