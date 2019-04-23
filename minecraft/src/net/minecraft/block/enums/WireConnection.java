package net.minecraft.block.enums;

import net.minecraft.util.SnakeCaseIdentifiable;

public enum WireConnection implements SnakeCaseIdentifiable {
	field_12686("up"),
	field_12689("side"),
	field_12687("none");

	private final String name;

	private WireConnection(String string2) {
		this.name = string2;
	}

	public String toString() {
		return this.toSnakeCase();
	}

	@Override
	public String toSnakeCase() {
		return this.name;
	}
}
