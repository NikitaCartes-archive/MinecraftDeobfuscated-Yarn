/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.feature.HeadFeatureRenderer;
import net.minecraft.client.render.entity.model.IllagerEntityModel;
import net.minecraft.entity.mob.IllagerEntity;

@Environment(value=EnvType.CLIENT)
public abstract class IllagerEntityRenderer<T extends IllagerEntity>
extends MobEntityRenderer<T, IllagerEntityModel<T>> {
    protected IllagerEntityRenderer(EntityRenderDispatcher entityRenderDispatcher, IllagerEntityModel<T> illagerEntityModel, float f) {
        super(entityRenderDispatcher, illagerEntityModel, f);
        this.addFeature(new HeadFeatureRenderer(this));
    }

    public IllagerEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher, new IllagerEntityModel(0.0f, 0.0f, 64, 64), 0.5f);
        this.addFeature(new HeadFeatureRenderer(this));
    }

    @Override
    protected void scale(T illagerEntity, float f) {
        float g = 0.9375f;
        GlStateManager.scalef(0.9375f, 0.9375f, 0.9375f);
    }
}

