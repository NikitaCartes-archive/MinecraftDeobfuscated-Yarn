/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.FrogEntityModel;
import net.minecraft.entity.passive.FrogEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class FrogEntityRenderer
extends MobEntityRenderer<FrogEntity, FrogEntityModel<FrogEntity>> {
    public FrogEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new FrogEntityModel(context.getPart(EntityModelLayers.FROG)), 0.3f);
    }

    @Override
    public Identifier getTexture(FrogEntity frogEntity) {
        return frogEntity.getVariant().texture();
    }
}

