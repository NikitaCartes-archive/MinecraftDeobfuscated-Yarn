package net.minecraft.block.enums;

import net.minecraft.util.SnakeCaseIdentifiable;

public enum BlockHalf implements SnakeCaseIdentifiable {
	field_12619("top"),
	field_12617("bottom");

	private final String name;

	private BlockHalf(String string2) {
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
