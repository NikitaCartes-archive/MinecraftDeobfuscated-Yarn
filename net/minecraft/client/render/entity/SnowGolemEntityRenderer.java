/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.feature.SnowmanPumpkinFeatureRenderer;
import net.minecraft.client.render.entity.model.SnowmanEntityModel;
import net.minecraft.entity.passive.SnowGolemEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class SnowGolemEntityRenderer
extends MobEntityRenderer<SnowGolemEntity, SnowmanEntityModel<SnowGolemEntity>> {
    private static final Identifier SKIN = new Identifier("textures/entity/snow_golem.png");

    public SnowGolemEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher, new SnowmanEntityModel(), 0.5f);
        this.addFeature(new SnowmanPumpkinFeatureRenderer(this));
    }

    @Override
    public Identifier getTexture(SnowGolemEntity snowGolemEntity) {
        return SKIN;
    }
}

