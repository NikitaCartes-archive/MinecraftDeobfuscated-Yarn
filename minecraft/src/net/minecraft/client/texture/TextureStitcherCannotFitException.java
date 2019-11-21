package net.minecraft.client.texture;

import java.util.Collection;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class TextureStitcherCannotFitException extends RuntimeException {
	private final Collection<Sprite.class_4727> sprites;

	public TextureStitcherCannotFitException(Sprite.class_4727 sprite, Collection<Sprite.class_4727> sprites) {
		super(
			String.format(
				"Unable to fit: %s - size: %dx%d - Maybe try a lower resolution resourcepack?", sprite.method_24121(), sprite.method_24123(), sprite.method_24125()
			)
		);
		this.sprites = sprites;
	}

	public Collection<Sprite.class_4727> getSprites() {
		return this.sprites;
	}
}
