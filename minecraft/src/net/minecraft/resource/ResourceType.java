package net.minecraft.resource;

public enum ResourceType {
	field_14188("assets"),
	field_14190("data");

	private final String name;

	private ResourceType(String string2) {
		this.name = string2;
	}

	public String getName() {
		return this.name;
	}
}
