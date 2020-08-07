package net.minecraft.block.enums;

import net.minecraft.util.StringIdentifiable;

public enum WallShape implements StringIdentifiable {
	field_22178("none"),
	field_22179("low"),
	field_22180("tall");

	private final String name;

	private WallShape(String name) {
		this.name = name;
	}

	public String toString() {
		return this.asString();
	}

	@Override
	public String asString() {
		return this.name;
	}
}
