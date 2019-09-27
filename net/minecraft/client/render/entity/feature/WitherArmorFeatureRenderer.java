/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4607;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.WitherEntityModel;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class WitherArmorFeatureRenderer
extends class_4607<WitherEntity, WitherEntityModel<WitherEntity>> {
    private static final Identifier SKIN = new Identifier("textures/entity/wither/wither_armor.png");
    private final WitherEntityModel<WitherEntity> model = new WitherEntityModel(0.5f);

    public WitherArmorFeatureRenderer(FeatureRendererContext<WitherEntity, WitherEntityModel<WitherEntity>> featureRendererContext) {
        super(featureRendererContext);
    }

    @Override
    protected float method_23202(float f) {
        return MathHelper.cos(f * 0.02f) * 3.0f;
    }

    @Override
    protected Identifier method_23201() {
        return SKIN;
    }

    @Override
    protected EntityModel<WitherEntity> method_23203() {
        return this.model;
    }
}

