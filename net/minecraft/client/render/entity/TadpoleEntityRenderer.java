/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.TadpoleEntityModel;
import net.minecraft.entity.passive.TadpoleEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class TadpoleEntityRenderer
extends MobEntityRenderer<TadpoleEntity, TadpoleEntityModel<TadpoleEntity>> {
    private static final Identifier TEXTURE = new Identifier("textures/entity/tadpole/tadpole.png");

    public TadpoleEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new TadpoleEntityModel(context.getPart(EntityModelLayers.TADPOLE)), 0.14f);
    }

    @Override
    public Identifier getTexture(TadpoleEntity tadpoleEntity) {
        return TEXTURE;
    }
}

