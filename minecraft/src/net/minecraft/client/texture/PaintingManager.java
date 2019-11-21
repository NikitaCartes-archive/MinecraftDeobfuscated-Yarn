package net.minecraft.client.texture;

import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.decoration.painting.PaintingMotive;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

@Environment(EnvType.CLIENT)
public class PaintingManager extends SpriteAtlasHolder {
	private static final Identifier PAINTING_BACK_ID = new Identifier("back");

	public PaintingManager(TextureManager textureManager) {
		super(textureManager, new Identifier("textures/atlas/paintings.png"), "painting");
	}

	@Override
	protected Stream<Identifier> getSprites() {
		return Stream.concat(Registry.MOTIVE.getIds().stream(), Stream.of(PAINTING_BACK_ID));
	}

	public Sprite getPaintingSprite(PaintingMotive motive) {
		return this.getSprite(Registry.MOTIVE.getId(motive));
	}

	public Sprite getBackSprite() {
		return this.getSprite(PAINTING_BACK_ID);
	}
}
