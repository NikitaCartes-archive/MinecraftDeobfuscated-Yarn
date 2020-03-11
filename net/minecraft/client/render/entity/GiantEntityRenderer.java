/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.feature.ArmorBipedFeatureRenderer;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.GiantEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.GiantEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class GiantEntityRenderer
extends MobEntityRenderer<GiantEntity, BipedEntityModel<GiantEntity>> {
    private static final Identifier TEXTURE = new Identifier("textures/entity/zombie/zombie.png");
    private final float scale;

    public GiantEntityRenderer(EntityRenderDispatcher dispatcher, float f) {
        super(dispatcher, new GiantEntityModel(), 0.5f * f);
        this.scale = f;
        this.addFeature(new HeldItemFeatureRenderer<GiantEntity, BipedEntityModel<GiantEntity>>(this));
        this.addFeature(new ArmorBipedFeatureRenderer<GiantEntity, BipedEntityModel<GiantEntity>, GiantEntityModel>(this, new GiantEntityModel(0.5f, true), new GiantEntityModel(1.0f, true)));
    }

    @Override
    protected void scale(GiantEntity giantEntity, MatrixStack matrixStack, float f) {
        matrixStack.scale(this.scale, this.scale, this.scale);
    }

    @Override
    public Identifier getTexture(GiantEntity giantEntity) {
        return TEXTURE;
    }
}

