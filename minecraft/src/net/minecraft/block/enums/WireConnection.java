package net.minecraft.block.enums;

import net.minecraft.util.StringIdentifiable;

public enum WireConnection implements StringIdentifiable {
	field_12686("up"),
	field_12689("side"),
	field_12687("none");

	private final String name;

	private WireConnection(String name) {
		this.name = name;
	}

	public String toString() {
		return this.asString();
	}

	@Override
	public String asString() {
		return this.name;
	}

	public boolean isConnected() {
		return this != field_12687;
	}
}
