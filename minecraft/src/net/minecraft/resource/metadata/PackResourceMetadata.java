package net.minecraft.resource.metadata;

import net.minecraft.network.chat.Component;

public class PackResourceMetadata {
	public static final PackResourceMetadataReader READER = new PackResourceMetadataReader();
	private final Component description;
	private final int packFormat;

	public PackResourceMetadata(Component component, int i) {
		this.description = component;
		this.packFormat = i;
	}

	public Component getDescription() {
		return this.description;
	}

	public int getPackFormat() {
		return this.packFormat;
	}
}
