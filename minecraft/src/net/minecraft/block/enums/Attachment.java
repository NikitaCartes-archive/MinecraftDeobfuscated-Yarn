package net.minecraft.block.enums;

import net.minecraft.util.StringIdentifiable;

public enum Attachment implements StringIdentifiable {
	FLOOR("floor"),
	CEILING("ceiling"),
	SINGLE_WALL("single_wall"),
	DOUBLE_WALL("double_wall");

	private final String name;

	private Attachment(String string2) {
		this.name = string2;
	}

	@Override
	public String asString() {
		return this.name;
	}
}
