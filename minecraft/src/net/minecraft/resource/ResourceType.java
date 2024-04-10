package net.minecraft.resource;

public enum ResourceType {
	CLIENT_RESOURCES("assets"),
	SERVER_DATA("data");

	private final String directory;

	private ResourceType(final String directory) {
		this.directory = directory;
	}

	public String getDirectory() {
		return this.directory;
	}
}
