package net.minecraft.client.texture;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

@Environment(EnvType.CLIENT)
public class StatusEffectSpriteManager extends SpriteAtlasHolder {
	public StatusEffectSpriteManager(TextureManager textureManager) {
		super(textureManager, SpriteAtlasTexture.STATUS_EFFECT_ATLAS_TEX, "textures/mob_effect");
	}

	@Override
	protected Iterable<Identifier> getSprites() {
		return Registry.STATUS_EFFECT.getIds();
	}

	public Sprite getSprite(StatusEffect statusEffect) {
		return this.getSprite(Registry.STATUS_EFFECT.getId(statusEffect));
	}
}
