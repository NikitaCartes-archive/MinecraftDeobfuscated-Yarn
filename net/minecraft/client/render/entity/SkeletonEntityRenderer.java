/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.BipedEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.SkeletonEntityModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.AbstractSkeletonEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class SkeletonEntityRenderer
extends BipedEntityRenderer<AbstractSkeletonEntity, SkeletonEntityModel<AbstractSkeletonEntity>> {
    private static final Identifier TEXTURE = new Identifier("textures/entity/skeleton/skeleton.png");

    public SkeletonEntityRenderer(EntityRendererFactory.Context context) {
        this(context, EntityModelLayers.SKELETON, EntityModelLayers.SKELETON_INNER_ARMOR, EntityModelLayers.SKELETON_OUTER_ARMOR);
    }

    public SkeletonEntityRenderer(EntityRendererFactory.Context ctx, EntityModelLayer layer, EntityModelLayer legArmorLayer, EntityModelLayer bodyArmorLayer) {
        super(ctx, new SkeletonEntityModel(ctx.getPart(layer)), 0.5f);
        this.addFeature(new ArmorFeatureRenderer(this, new SkeletonEntityModel(ctx.getPart(legArmorLayer)), new SkeletonEntityModel(ctx.getPart(bodyArmorLayer)), ctx.getModelManager()));
    }

    @Override
    public Identifier getTexture(AbstractSkeletonEntity abstractSkeletonEntity) {
        return TEXTURE;
    }

    @Override
    protected boolean isShaking(AbstractSkeletonEntity abstractSkeletonEntity) {
        return abstractSkeletonEntity.isShaking();
    }

    @Override
    protected /* synthetic */ boolean isShaking(LivingEntity entity) {
        return this.isShaking((AbstractSkeletonEntity)entity);
    }
}

