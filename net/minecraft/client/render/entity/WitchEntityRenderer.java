/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
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

    public void method_4155(WitchEntity witchEntity, double d, double e, double f, float g, float h) {
        ((WitchEntityModel)this.model).method_2840(!witchEntity.getMainHandStack().isEmpty());
        super.method_4072(witchEntity, d, e, f, g, h);
    }

    protected Identifier method_4154(WitchEntity witchEntity) {
        return SKIN;
    }

    protected void method_4157(WitchEntity witchEntity, float f) {
        float g = 0.9375f;
        GlStateManager.scalef(0.9375f, 0.9375f, 0.9375f);
    }
}

