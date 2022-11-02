/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.CamelEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.entity.passive.CamelEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class CamelEntityRenderer
extends MobEntityRenderer<CamelEntity, CamelEntityModel<CamelEntity>> {
    private static final Identifier TEXTURE = new Identifier("textures/entity/camel/camel.png");

    public CamelEntityRenderer(EntityRendererFactory.Context ctx, EntityModelLayer layer) {
        super(ctx, new CamelEntityModel(ctx.getPart(layer)), 0.7f);
    }

    @Override
    public Identifier getTexture(CamelEntity camelEntity) {
        return TEXTURE;
    }
}

