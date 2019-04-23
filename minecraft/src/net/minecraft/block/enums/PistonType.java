package net.minecraft.block.enums;

import net.minecraft.util.SnakeCaseIdentifiable;

public enum PistonType implements SnakeCaseIdentifiable {
	field_12637("normal"),
	field_12634("sticky");

	private final String name;

	private PistonType(String string2) {
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
