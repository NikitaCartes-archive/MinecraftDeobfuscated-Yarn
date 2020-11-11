/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.CowEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class CowEntityRenderer
extends MobEntityRenderer<CowEntity, CowEntityModel<CowEntity>> {
    private static final Identifier TEXTURE = new Identifier("textures/entity/cow/cow.png");

    public CowEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new CowEntityModel(context.getPart(EntityModelLayers.COW)), 0.7f);
    }

    @Override
    public Identifier getTexture(CowEntity cowEntity) {
        return TEXTURE;
    }
}

