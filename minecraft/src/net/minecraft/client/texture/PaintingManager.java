package net.minecraft.client.texture;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.decoration.painting.PaintingVariant;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class PaintingManager extends SpriteAtlasHolder {
	private static final Identifier PAINTING_BACK_ID = new Identifier("back");

	public PaintingManager(TextureManager manager) {
		super(manager, new Identifier("textures/atlas/paintings.png"), new Identifier("paintings"));
	}

	public Sprite getPaintingSprite(PaintingVariant variant) {
		return this.getSprite(variant.assetId());
	}

	public Sprite getBackSprite() {
		return this.getSprite(PAINTING_BACK_ID);
	}
}
