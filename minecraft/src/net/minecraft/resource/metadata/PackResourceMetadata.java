package net.minecraft.resource.metadata;

import net.minecraft.text.Text;

public class PackResourceMetadata {
	public static final PackResourceMetadataReader READER = new PackResourceMetadataReader();
	private final Text description;
	private final int packFormat;

	public PackResourceMetadata(Text text, int i) {
		this.description = text;
		this.packFormat = i;
	}

	public Text getDescription() {
		return this.description;
	}

	public int getPackFormat() {
		return this.packFormat;
	}
}
