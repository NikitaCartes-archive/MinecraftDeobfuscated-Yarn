package net.minecraft.block.enums;

import net.minecraft.util.SnakeCaseIdentifiable;

public enum DoubleBlockHalf implements SnakeCaseIdentifiable {
	field_12609,
	field_12607;

	public String toString() {
		return this.toSnakeCase();
	}

	@Override
	public String toSnakeCase() {
		return this == field_12609 ? "upper" : "lower";
	}
}
