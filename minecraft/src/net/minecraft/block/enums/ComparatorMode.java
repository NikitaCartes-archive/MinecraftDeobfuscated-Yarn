package net.minecraft.block.enums;

import net.minecraft.util.SnakeCaseIdentifiable;

public enum ComparatorMode implements SnakeCaseIdentifiable {
	field_12576("compare"),
	field_12578("subtract");

	private final String name;

	private ComparatorMode(String string2) {
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
