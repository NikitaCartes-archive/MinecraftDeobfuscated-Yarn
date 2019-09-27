/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4587;
import net.minecraft.class_4597;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.feature.WitchHeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.WitchEntityModel;
import net.minecraft.entity.mob.WitchEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class WitchEntityRenderer
extends MobEntityRenderer<WitchEntity, WitchEntityModel<WitchEntity>> {
    private static final Identifier SKIN = new Identifier("textures/entity/witch.png");

    public WitchEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher, new WitchEntityModel(0.0f), 0.5f);
        this.addFeature(new WitchHeldItemFeatureRenderer<WitchEntity>(this));
    }

    public void method_4155(WitchEntity witchEntity, double d, double e, double f, float g, float h, class_4587 arg, class_4597 arg2) {
        ((WitchEntityModel)this.model).method_2840(!witchEntity.getMainHandStack().isEmpty());
        super.method_4072(witchEntity, d, e, f, g, h, arg, arg2);
    }

    public Identifier method_4154(WitchEntity witchEntity) {
        return SKIN;
    }

    protected void method_4157(WitchEntity witchEntity, class_4587 arg, float f) {
        float g = 0.9375f;
        arg.method_22905(0.9375f, 0.9375f, 0.9375f);
    }
}

