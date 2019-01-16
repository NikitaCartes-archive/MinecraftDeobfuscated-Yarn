package net.minecraft.resource;

public enum ResourceType {
	ASSETS("assets"),
	DATA("data");

	private final String name;

	private ResourceType(String string2) {
		this.name = string2;
	}

	public String getName() {
		return this.name;
	}
}
