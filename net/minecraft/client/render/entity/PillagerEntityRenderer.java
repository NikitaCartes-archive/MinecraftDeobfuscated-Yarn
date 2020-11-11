/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.IllagerEntityRenderer;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.IllagerEntityModel;
import net.minecraft.entity.mob.PillagerEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class PillagerEntityRenderer
extends IllagerEntityRenderer<PillagerEntity> {
    private static final Identifier TEXTURE = new Identifier("textures/entity/illager/pillager.png");

    public PillagerEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new IllagerEntityModel(context.getPart(EntityModelLayers.PILLAGER)), 0.5f);
        this.addFeature(new HeldItemFeatureRenderer<PillagerEntity, IllagerEntityModel<PillagerEntity>>(this));
    }

    @Override
    public Identifier getTexture(PillagerEntity pillagerEntity) {
        return TEXTURE;
    }
}

