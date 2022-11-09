package net.minecraft.client.texture;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class StatusEffectSpriteManager extends SpriteAtlasHolder {
	public StatusEffectSpriteManager(TextureManager textureManager) {
		super(textureManager, new Identifier("textures/atlas/mob_effects.png"), "mob_effect");
	}

	public Sprite getSprite(StatusEffect effect) {
		return this.getSprite(Registries.STATUS_EFFECT.getId(effect));
	}
}
