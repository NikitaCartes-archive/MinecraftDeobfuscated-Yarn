/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5617;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.GiantEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.GiantEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class GiantEntityRenderer
extends MobEntityRenderer<GiantEntity, BipedEntityModel<GiantEntity>> {
    private static final Identifier TEXTURE = new Identifier("textures/entity/zombie/zombie.png");
    private final float scale;

    public GiantEntityRenderer(class_5617.class_5618 arg, float f) {
        super(arg, new GiantEntityModel(arg.method_32167(EntityModelLayers.GIANT)), 0.5f * f);
        this.scale = f;
        this.addFeature(new HeldItemFeatureRenderer<GiantEntity, BipedEntityModel<GiantEntity>>(this));
        this.addFeature(new ArmorFeatureRenderer<GiantEntity, BipedEntityModel<GiantEntity>, GiantEntityModel>(this, new GiantEntityModel(arg.method_32167(EntityModelLayers.GIANT_INNER_ARMOR)), new GiantEntityModel(arg.method_32167(EntityModelLayers.GIANT_OUTER_ARMOR))));
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

