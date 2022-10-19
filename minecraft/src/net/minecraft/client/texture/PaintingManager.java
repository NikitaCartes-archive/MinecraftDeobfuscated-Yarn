package net.minecraft.client.texture;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.decoration.painting.PaintingVariant;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

@Environment(EnvType.CLIENT)
public class PaintingManager extends SpriteAtlasHolder {
	private static final Identifier PAINTING_BACK_ID = new Identifier("back");

	public PaintingManager(TextureManager manager) {
		super(manager, new Identifier("textures/atlas/paintings.png"), "painting");
	}

	public Sprite getPaintingSprite(PaintingVariant variant) {
		return this.getSprite(Registry.PAINTING_VARIANT.getId(variant));
	}

	public Sprite getBackSprite() {
		return this.getSprite(PAINTING_BACK_ID);
	}
}
