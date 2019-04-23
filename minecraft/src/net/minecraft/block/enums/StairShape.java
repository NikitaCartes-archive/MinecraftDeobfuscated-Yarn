package net.minecraft.block.enums;

import net.minecraft.util.SnakeCaseIdentifiable;

public enum StairShape implements SnakeCaseIdentifiable {
	field_12710("straight"),
	field_12712("inner_left"),
	field_12713("inner_right"),
	field_12708("outer_left"),
	field_12709("outer_right");

	private final String name;

	private StairShape(String string2) {
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
