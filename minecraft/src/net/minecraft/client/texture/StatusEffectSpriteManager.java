package net.minecraft.client.texture;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

@Environment(EnvType.CLIENT)
public class StatusEffectSpriteManager extends SpriteAtlasHolder {
	public StatusEffectSpriteManager(TextureManager textureManager) {
		super(textureManager, SpriteAtlasTexture.field_18229, "textures/mob_effect");
	}

	@Override
	protected Iterable<Identifier> getSprites() {
		return Registry.STATUS_EFFECT.getIds();
	}

	public Sprite getSprite(StatusEffect statusEffect) {
		return this.method_18667(Registry.STATUS_EFFECT.method_10221(statusEffect));
	}
}
