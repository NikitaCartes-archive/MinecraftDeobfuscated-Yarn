package net.minecraft.client.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * Internal class used by {@link TexturedModelData}.
 */
@Environment(EnvType.CLIENT)
public class TextureDimensions {
	final int width;
	final int height;

	public TextureDimensions(int width, int height) {
		this.width = width;
		this.height = height;
	}
}
