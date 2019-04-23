/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.IllagerBeastEntityModel;
import net.minecraft.entity.mob.RavagerEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class IllagerBeastEntityRenderer
extends MobEntityRenderer<RavagerEntity, IllagerBeastEntityModel> {
    private static final Identifier SKIN = new Identifier("textures/entity/illager/ravager.png");

    public IllagerBeastEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher, new IllagerBeastEntityModel(), 1.1f);
    }

    protected Identifier method_3984(RavagerEntity ravagerEntity) {
        return SKIN;
    }
}

