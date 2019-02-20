package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

@Environment(EnvType.CLIENT)
public class class_4074 extends class_4075 {
	public class_4074(TextureManager textureManager) {
		super(textureManager, SpriteAtlasTexture.field_18229, "textures/mob_effect");
	}

	@Override
	protected Iterable<Identifier> method_18665() {
		return Registry.STATUS_EFFECT.getIds();
	}

	public Sprite method_18663(StatusEffect statusEffect) {
		return this.method_18667(Registry.STATUS_EFFECT.getId(statusEffect));
	}
}
