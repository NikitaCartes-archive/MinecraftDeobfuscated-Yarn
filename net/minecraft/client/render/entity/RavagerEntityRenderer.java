/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.RavagerEntityModel;
import net.minecraft.entity.mob.RavagerEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class RavagerEntityRenderer
extends MobEntityRenderer<RavagerEntity, RavagerEntityModel> {
    private static final Identifier TEXTURE = new Identifier("textures/entity/illager/ravager.png");

    public RavagerEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new RavagerEntityModel(context.getPart(EntityModelLayers.RAVAGER)), 1.1f);
    }

    @Override
    public Identifier getTexture(RavagerEntity ravagerEntity) {
        return TEXTURE;
    }
}

