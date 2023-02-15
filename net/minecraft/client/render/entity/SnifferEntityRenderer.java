/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.SnifferModel;
import net.minecraft.entity.passive.SnifferEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class SnifferEntityRenderer
extends MobEntityRenderer<SnifferEntity, SnifferModel<SnifferEntity>> {
    private static final Identifier TEXTURE = new Identifier("textures/entity/sniffer/sniffer.png");

    public SnifferEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new SnifferModel(context.getPart(EntityModelLayers.SNIFFER)), 1.1f);
    }

    @Override
    public Identifier getTexture(SnifferEntity snifferEntity) {
        return TEXTURE;
    }
}

