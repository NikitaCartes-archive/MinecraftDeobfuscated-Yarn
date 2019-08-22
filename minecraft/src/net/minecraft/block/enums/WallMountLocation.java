package net.minecraft.block.enums;

import net.minecraft.util.StringIdentifiable;

public enum WallMountLocation implements StringIdentifiable {
	FLOOR("floor"),
	WALL("wall"),
	CEILING("ceiling");

	private final String name;

	private WallMountLocation(String string2) {
		this.name = string2;
	}

	@Override
	public String asString() {
		return this.name;
	}
}
