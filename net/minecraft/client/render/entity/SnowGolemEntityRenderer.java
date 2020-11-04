/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5617;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.feature.SnowmanPumpkinFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.SnowGolemEntityModel;
import net.minecraft.entity.passive.SnowGolemEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class SnowGolemEntityRenderer
extends MobEntityRenderer<SnowGolemEntity, SnowGolemEntityModel<SnowGolemEntity>> {
    private static final Identifier TEXTURE = new Identifier("textures/entity/snow_golem.png");

    public SnowGolemEntityRenderer(class_5617.class_5618 arg) {
        super(arg, new SnowGolemEntityModel(arg.method_32167(EntityModelLayers.SNOW_GOLEM)), 0.5f);
        this.addFeature(new SnowmanPumpkinFeatureRenderer(this));
    }

    @Override
    public Identifier getTexture(SnowGolemEntity snowGolemEntity) {
        return TEXTURE;
    }
}

