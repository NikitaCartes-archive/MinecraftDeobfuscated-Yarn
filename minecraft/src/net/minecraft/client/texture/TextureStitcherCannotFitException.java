package net.minecraft.client.texture;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class TextureStitcherCannotFitException extends RuntimeException {
	private final TextureStitcher.Holder holder;

	public TextureStitcherCannotFitException(TextureStitcher.Holder holder, String string) {
		super(string);
		this.holder = holder;
	}
}
