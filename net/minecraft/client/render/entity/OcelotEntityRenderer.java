/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.OcelotEntityModel;
import net.minecraft.entity.passive.OcelotEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class OcelotEntityRenderer
extends MobEntityRenderer<OcelotEntity, OcelotEntityModel<OcelotEntity>> {
    private static final Identifier TEXTURE = new Identifier("textures/entity/cat/ocelot.png");

    public OcelotEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher, new OcelotEntityModel(0.0f), 0.4f);
    }

    @Override
    public Identifier getTexture(OcelotEntity ocelotEntity) {
        return TEXTURE;
    }
}

