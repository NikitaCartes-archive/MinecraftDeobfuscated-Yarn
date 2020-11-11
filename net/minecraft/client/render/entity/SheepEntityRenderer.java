/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.feature.SheepWoolFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.SheepEntityModel;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class SheepEntityRenderer
extends MobEntityRenderer<SheepEntity, SheepEntityModel<SheepEntity>> {
    private static final Identifier TEXTURE = new Identifier("textures/entity/sheep/sheep.png");

    public SheepEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new SheepEntityModel(context.getPart(EntityModelLayers.SHEEP)), 0.7f);
        this.addFeature(new SheepWoolFeatureRenderer(this, context.getModelLoader()));
    }

    @Override
    public Identifier getTexture(SheepEntity sheepEntity) {
        return TEXTURE;
    }
}

