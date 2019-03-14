package net.minecraft.resource.metadata;

import net.minecraft.text.TextComponent;

public class PackResourceMetadata {
	public static final PackResourceMetadataReader READER = new PackResourceMetadataReader();
	private final TextComponent description;
	private final int packFormat;

	public PackResourceMetadata(TextComponent textComponent, int i) {
		this.description = textComponent;
		this.packFormat = i;
	}

	public TextComponent getDescription() {
		return this.description;
	}

	public int getPackFormat() {
		return this.packFormat;
	}
}
