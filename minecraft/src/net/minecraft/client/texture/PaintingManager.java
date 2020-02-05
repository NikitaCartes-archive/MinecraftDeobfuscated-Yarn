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

	public PaintingManager(TextureManager manager) {
		super(manager, new Identifier("textures/atlas/paintings.png"), "painting");
	}

	@Override
	protected Stream<Identifier> getSprites() {
		return Stream.concat(Registry.PAINTING_MOTIVE.getIds().stream(), Stream.of(PAINTING_BACK_ID));
	}

	public Sprite getPaintingSprite(PaintingMotive motive) {
		return this.getSprite(Registry.PAINTING_MOTIVE.getId(motive));
	}

	public Sprite getBackSprite() {
		return this.getSprite(PAINTING_BACK_ID);
	}
}
