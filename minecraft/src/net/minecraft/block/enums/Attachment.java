package net.minecraft.block.enums;

import net.minecraft.util.SnakeCaseIdentifiable;

public enum Attachment implements SnakeCaseIdentifiable {
	field_17098("floor"),
	field_17099("ceiling"),
	field_17100("single_wall"),
	field_17101("double_wall");

	private final String name;

	private Attachment(String string2) {
		this.name = string2;
	}

	@Override
	public String toSnakeCase() {
		return this.name;
	}
}
