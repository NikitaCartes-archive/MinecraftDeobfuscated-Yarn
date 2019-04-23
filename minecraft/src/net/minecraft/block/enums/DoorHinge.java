package net.minecraft.block.enums;

import net.minecraft.util.SnakeCaseIdentifiable;

public enum DoorHinge implements SnakeCaseIdentifiable {
	field_12588,
	field_12586;

	public String toString() {
		return this.toSnakeCase();
	}

	@Override
	public String toSnakeCase() {
		return this == field_12588 ? "left" : "right";
	}
}
