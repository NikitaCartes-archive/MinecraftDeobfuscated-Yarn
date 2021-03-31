/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.GoatEntityModel;
import net.minecraft.entity.passive.GoatEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class GoatEntityRenderer
extends MobEntityRenderer<GoatEntity, GoatEntityModel<GoatEntity>> {
    private static final Identifier TEXTURE = new Identifier("textures/entity/goat/goat.png");

    public GoatEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new GoatEntityModel(context.getPart(EntityModelLayers.GOAT)), 0.7f);
    }

    @Override
    public Identifier getTexture(GoatEntity goatEntity) {
        return TEXTURE;
    }
}

