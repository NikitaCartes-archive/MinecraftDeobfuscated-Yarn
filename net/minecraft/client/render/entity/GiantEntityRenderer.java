/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRendererFactory;
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

    public GiantEntityRenderer(EntityRendererFactory.Context ctx, float scale) {
        super(ctx, new GiantEntityModel(ctx.getPart(EntityModelLayers.GIANT)), 0.5f * scale);
        this.scale = scale;
        this.addFeature(new HeldItemFeatureRenderer<GiantEntity, BipedEntityModel<GiantEntity>>(this, ctx.getHeldItemRenderer()));
        this.addFeature(new ArmorFeatureRenderer<GiantEntity, BipedEntityModel<GiantEntity>, GiantEntityModel>(this, new GiantEntityModel(ctx.getPart(EntityModelLayers.GIANT_INNER_ARMOR)), new GiantEntityModel(ctx.getPart(EntityModelLayers.GIANT_OUTER_ARMOR))));
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

