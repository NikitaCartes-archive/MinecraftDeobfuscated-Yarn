package net.minecraft.client.texture;

import com.google.common.collect.Iterables;
import java.util.Collections;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.decoration.painting.PaintingMotive;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

@Environment(EnvType.CLIENT)
public class PaintingManager extends SpriteAtlasHolder {
	private static final Identifier PAINTING_BACK_ID = new Identifier("back");

	public PaintingManager(TextureManager textureManager) {
		super(textureManager, SpriteAtlasTexture.PAINTING_ATLAS_TEX, "textures/painting");
	}

	@Override
	protected Iterable<Identifier> getSprites() {
		return Iterables.concat(Registry.MOTIVE.getIds(), Collections.singleton(PAINTING_BACK_ID));
	}

	public Sprite getPaintingSprite(PaintingMotive motive) {
		return this.getSprite(Registry.MOTIVE.getId(motive));
	}

	public Sprite getBackSprite() {
		return this.getSprite(PAINTING_BACK_ID);
	}
}
