/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.BipedEntityRenderer;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.feature.ArmorBipedFeatureRenderer;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.StrayEntityModel;
import net.minecraft.entity.mob.AbstractSkeletonEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class SkeletonEntityRenderer
extends BipedEntityRenderer<AbstractSkeletonEntity, StrayEntityModel<AbstractSkeletonEntity>> {
    private static final Identifier SKIN = new Identifier("textures/entity/skeleton/skeleton.png");

    public SkeletonEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher, new StrayEntityModel(), 0.5f);
        this.addFeature(new HeldItemFeatureRenderer<AbstractSkeletonEntity, StrayEntityModel<AbstractSkeletonEntity>>(this));
        this.addFeature(new ArmorBipedFeatureRenderer(this, new StrayEntityModel(0.5f, true), new StrayEntityModel(1.0f, true)));
    }

    public Identifier method_4119(AbstractSkeletonEntity abstractSkeletonEntity) {
        return SKIN;
    }
}

