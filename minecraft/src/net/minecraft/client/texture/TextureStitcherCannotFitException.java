package net.minecraft.client.texture;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class TextureStitcherCannotFitException extends RuntimeException {
	public TextureStitcherCannotFitException(Sprite sprite) {
		super(
			String.format("Unable to fit: %s - size: %dx%d - Maybe try a lower resolution resourcepack?", sprite.method_4598(), sprite.getWidth(), sprite.getHeight())
		);
	}
}
