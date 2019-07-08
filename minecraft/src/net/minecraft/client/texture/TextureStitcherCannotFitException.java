package net.minecraft.client.texture;

import java.util.Collection;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class TextureStitcherCannotFitException extends RuntimeException {
	private final Collection<Sprite> field_20311;

	public TextureStitcherCannotFitException(Sprite sprite, Collection<Sprite> collection) {
		super(String.format("Unable to fit: %s - size: %dx%d - Maybe try a lower resolution resourcepack?", sprite.getId(), sprite.getWidth(), sprite.getHeight()));
		this.field_20311 = collection;
	}

	public Collection<Sprite> method_21687() {
		return this.field_20311;
	}
}
