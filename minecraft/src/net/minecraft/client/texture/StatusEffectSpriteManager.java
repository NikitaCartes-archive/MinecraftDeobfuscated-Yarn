package net.minecraft.client.texture;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class StatusEffectSpriteManager extends SpriteAtlasHolder {
	public StatusEffectSpriteManager(TextureManager textureManager) {
		super(textureManager, Identifier.ofVanilla("textures/atlas/mob_effects.png"), Identifier.ofVanilla("mob_effects"));
	}

	public Sprite getSprite(RegistryEntry<StatusEffect> effect) {
		return this.getSprite((Identifier)effect.getKey().map(RegistryKey::getValue).orElseGet(MissingSprite::getMissingSpriteId));
	}
}
