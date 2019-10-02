package net.minecraft.resource;

public enum ResourceType {
	CLIENT_RESOURCES("assets"),
	SERVER_DATA("data");

	private final String directory;

	private ResourceType(String string2) {
		this.directory = string2;
	}

	public String getDirectory() {
		return this.directory;
	}
}
