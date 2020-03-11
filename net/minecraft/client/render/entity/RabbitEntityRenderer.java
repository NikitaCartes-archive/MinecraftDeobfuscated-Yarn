/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.RabbitEntityModel;
import net.minecraft.entity.passive.RabbitEntity;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class RabbitEntityRenderer
extends MobEntityRenderer<RabbitEntity, RabbitEntityModel<RabbitEntity>> {
    private static final Identifier BROWN_TEXTURE = new Identifier("textures/entity/rabbit/brown.png");
    private static final Identifier WHITE_TEXTURE = new Identifier("textures/entity/rabbit/white.png");
    private static final Identifier BLACK_TEXTURE = new Identifier("textures/entity/rabbit/black.png");
    private static final Identifier GOLD_TEXTURE = new Identifier("textures/entity/rabbit/gold.png");
    private static final Identifier SALT_TEXTURE = new Identifier("textures/entity/rabbit/salt.png");
    private static final Identifier WHITE_SPOTTED_TEXTURE = new Identifier("textures/entity/rabbit/white_splotched.png");
    private static final Identifier TOAST_TEXTURE = new Identifier("textures/entity/rabbit/toast.png");
    private static final Identifier CAERBANNOG_TEXTURE = new Identifier("textures/entity/rabbit/caerbannog.png");

    public RabbitEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher, new RabbitEntityModel(), 0.3f);
    }

    @Override
    public Identifier getTexture(RabbitEntity rabbitEntity) {
        String string = Formatting.strip(rabbitEntity.getName().getString());
        if (string != null && "Toast".equals(string)) {
            return TOAST_TEXTURE;
        }
        switch (rabbitEntity.getRabbitType()) {
            default: {
                return BROWN_TEXTURE;
            }
            case 1: {
                return WHITE_TEXTURE;
            }
            case 2: {
                return BLACK_TEXTURE;
            }
            case 4: {
                return GOLD_TEXTURE;
            }
            case 5: {
                return SALT_TEXTURE;
            }
            case 3: {
                return WHITE_SPOTTED_TEXTURE;
            }
            case 99: 
        }
        return CAERBANNOG_TEXTURE;
    }
}

