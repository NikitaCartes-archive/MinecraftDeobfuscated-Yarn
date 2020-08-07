package net.minecraft.resource;

public enum ResourceType {
	field_14188("assets"),
	field_14190("data");

	private final String directory;

	private ResourceType(String name) {
		this.directory = name;
	}

	public String getDirectory() {
		return this.directory;
	}
}
