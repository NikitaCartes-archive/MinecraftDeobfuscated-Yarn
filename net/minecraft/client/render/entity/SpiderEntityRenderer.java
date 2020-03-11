/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.feature.SpiderEyesFeatureRenderer;
import net.minecraft.client.render.entity.model.SpiderEntityModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.SpiderEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class SpiderEntityRenderer<T extends SpiderEntity>
extends MobEntityRenderer<T, SpiderEntityModel<T>> {
    private static final Identifier TEXTURE = new Identifier("textures/entity/spider/spider.png");

    public SpiderEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher, new SpiderEntityModel(), 0.8f);
        this.addFeature(new SpiderEyesFeatureRenderer(this));
    }

    @Override
    protected float getLyingAngle(T spiderEntity) {
        return 180.0f;
    }

    @Override
    public Identifier getTexture(T spiderEntity) {
        return TEXTURE;
    }

    @Override
    protected /* synthetic */ float getLyingAngle(LivingEntity entity) {
        return this.getLyingAngle((T)((SpiderEntity)entity));
    }
}

