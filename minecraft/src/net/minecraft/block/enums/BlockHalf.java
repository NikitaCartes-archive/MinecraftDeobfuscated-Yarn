package net.minecraft.block.enums;

import net.minecraft.util.StringIdentifiable;

public enum BlockHalf implements StringIdentifiable {
	field_12619("top"),
	field_12617("bottom");

	private final String name;

	private BlockHalf(String name) {
		this.name = name;
	}

	public String toString() {
		return this.name;
	}

	@Override
	public String asString() {
		return this.name;
	}
}
