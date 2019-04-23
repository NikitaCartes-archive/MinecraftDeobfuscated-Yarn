package net.minecraft.block.enums;

import net.minecraft.util.SnakeCaseIdentifiable;

public enum SlabType implements SnakeCaseIdentifiable {
	field_12679("top"),
	field_12681("bottom"),
	field_12682("double");

	private final String name;

	private SlabType(String string2) {
		this.name = string2;
	}

	public String toString() {
		return this.name;
	}

	@Override
	public String toSnakeCase() {
		return this.name;
	}
}
