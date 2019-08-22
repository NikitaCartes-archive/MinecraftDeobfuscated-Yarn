/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.feature.HeadFeatureRenderer;
import net.minecraft.client.render.entity.model.EvilVillagerEntityModel;
import net.minecraft.entity.mob.IllagerEntity;

@Environment(value=EnvType.CLIENT)
public abstract class IllagerEntityRenderer<T extends IllagerEntity>
extends MobEntityRenderer<T, EvilVillagerEntityModel<T>> {
    protected IllagerEntityRenderer(EntityRenderDispatcher entityRenderDispatcher, EvilVillagerEntityModel<T> evilVillagerEntityModel, float f) {
        super(entityRenderDispatcher, evilVillagerEntityModel, f);
        this.addFeature(new HeadFeatureRenderer(this));
    }

    public IllagerEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher, new EvilVillagerEntityModel(0.0f, 0.0f, 64, 64), 0.5f);
        this.addFeature(new HeadFeatureRenderer(this));
    }

    protected void method_16460(T illagerEntity, float f) {
        float g = 0.9375f;
        RenderSystem.scalef(0.9375f, 0.9375f, 0.9375f);
    }
}

