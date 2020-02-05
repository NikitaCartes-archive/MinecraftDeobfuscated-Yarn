/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.HoglinEntityModel;
import net.minecraft.entity.mob.HoglinEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class HoglinEntityRenderer
extends MobEntityRenderer<HoglinEntity, HoglinEntityModel> {
    private static final Identifier SKIN = new Identifier("textures/entity/hoglin/hoglin.png");

    public HoglinEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher, new HoglinEntityModel(), 0.7f);
    }

    @Override
    public Identifier getTexture(HoglinEntity hoglinEntity) {
        return SKIN;
    }
}

