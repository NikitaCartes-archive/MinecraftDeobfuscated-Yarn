package net.minecraft.client.resource.metadata;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class TextureResourceMetadata {
	public static final TextureResourceMetadataReader READER = new TextureResourceMetadataReader();
	public static final boolean field_32980 = false;
	public static final boolean field_32981 = false;
	private final boolean blur;
	private final boolean clamp;

	public TextureResourceMetadata(boolean blur, boolean clamp) {
		this.blur = blur;
		this.clamp = clamp;
	}

	public boolean shouldBlur() {
		return this.blur;
	}

	public boolean shouldClamp() {
		return this.clamp;
	}
}
