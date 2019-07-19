/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.IllagerEntityRenderer;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.IllagerEntityModel;
import net.minecraft.entity.mob.SpellcastingIllagerEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class EvokerIllagerEntityRenderer<T extends SpellcastingIllagerEntity>
extends IllagerEntityRenderer<T> {
    private static final Identifier EVOKER_TEXTURE = new Identifier("textures/entity/illager/evoker.png");

    public EvokerIllagerEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher, new IllagerEntityModel(0.0f, 0.0f, 64, 64), 0.5f);
        this.addFeature(new HeldItemFeatureRenderer<T, IllagerEntityModel<T>>(this){

            @Override
            public void render(T spellcastingIllagerEntity, float f, float g, float h, float i, float j, float k, float l) {
                if (((SpellcastingIllagerEntity)spellcastingIllagerEntity).isSpellcasting()) {
                    super.render(spellcastingIllagerEntity, f, g, h, i, j, k, l);
                }
            }
        });
    }

    @Override
    protected Identifier getTexture(T spellcastingIllagerEntity) {
        return EVOKER_TEXTURE;
    }
}

