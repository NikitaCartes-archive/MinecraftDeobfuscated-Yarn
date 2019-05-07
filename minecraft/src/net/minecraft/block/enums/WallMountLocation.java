package net.minecraft.block.enums;

import net.minecraft.util.StringIdentifiable;

public enum WallMountLocation implements StringIdentifiable {
	field_12475("floor"),
	field_12471("wall"),
	field_12473("ceiling");

	private final String name;

	private WallMountLocation(String string2) {
		this.name = string2;
	}

	@Override
	public String asString() {
		return this.name;
	}
}
