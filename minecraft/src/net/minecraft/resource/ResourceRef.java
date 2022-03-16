package net.minecraft.resource;

import java.io.IOException;

public class ResourceRef {
	private final String packName;
	private final ResourceRef.Opener opener;

	public ResourceRef(String name, ResourceRef.Opener opener) {
		this.packName = name;
		this.opener = opener;
	}

	public String getPackName() {
		return this.packName;
	}

	public Resource open() throws IOException {
		return this.opener.open();
	}

	@FunctionalInterface
	public interface Opener {
		Resource open() throws IOException;
	}
}
