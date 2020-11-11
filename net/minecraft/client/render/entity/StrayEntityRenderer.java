/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.SkeletonEntityRenderer;
import net.minecraft.client.render.entity.feature.StrayOverlayFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.SkeletonEntityModel;
import net.minecraft.entity.mob.AbstractSkeletonEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class StrayEntityRenderer
extends SkeletonEntityRenderer {
    private static final Identifier TEXTURE = new Identifier("textures/entity/skeleton/stray.png");

    public StrayEntityRenderer(EntityRendererFactory.Context context) {
        super(context, EntityModelLayers.STRAY, EntityModelLayers.STRAY_INNER_ARMOR, EntityModelLayers.STRAY_OUTER_ARMOR);
        this.addFeature(new StrayOverlayFeatureRenderer<AbstractSkeletonEntity, SkeletonEntityModel<AbstractSkeletonEntity>>(this, context.getModelLoader()));
    }

    @Override
    public Identifier getTexture(AbstractSkeletonEntity abstractSkeletonEntity) {
        return TEXTURE;
    }
}

