package net.minecraft.client.texture;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class StatusEffectSpriteManager extends SpriteAtlasHolder {
	public StatusEffectSpriteManager(TextureManager textureManager) {
		super(textureManager, new Identifier("textures/atlas/mob_effects.png"), new Identifier("mob_effects"));
	}

	public Sprite getSprite(RegistryEntry<StatusEffect> effect) {
		if (effect == StatusEffects.BAD_OMEN) {
			ClientWorld clientWorld = MinecraftClient.getInstance().world;
			if (clientWorld != null && clientWorld.getEnabledFeatures().contains(FeatureFlags.UPDATE_1_21)) {
				return this.getSprite(new Identifier("bad_omen_121"));
			}
		}

		return this.getSprite((Identifier)effect.getKey().map(RegistryKey::getValue).orElseGet(MissingSprite::getMissingSpriteId));
	}
}
