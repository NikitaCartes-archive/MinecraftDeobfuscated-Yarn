package net.minecraft.client.resource.metadata;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class TextureResourceMetadata {
	public static final TextureResourceMetadataReader field_5344 = new TextureResourceMetadataReader();
	private final boolean blur;
	private final boolean clamp;

	public TextureResourceMetadata(boolean bl, boolean bl2) {
		this.blur = bl;
		this.clamp = bl2;
	}

	public boolean shouldBlur() {
		return this.blur;
	}

	public boolean shouldClamp() {
		return this.clamp;
	}
}
