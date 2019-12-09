/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.texture;

import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasHolder;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

@Environment(value=EnvType.CLIENT)
public class StatusEffectSpriteManager
extends SpriteAtlasHolder {
    public StatusEffectSpriteManager(TextureManager textureManager) {
        super(textureManager, new Identifier("textures/atlas/mob_effects.png"), "mob_effect");
    }

    @Override
    protected Stream<Identifier> getSprites() {
        return Registry.STATUS_EFFECT.getIds().stream();
    }

    public Sprite getSprite(StatusEffect effect) {
        return this.getSprite(Registry.STATUS_EFFECT.getId(effect));
    }
}

