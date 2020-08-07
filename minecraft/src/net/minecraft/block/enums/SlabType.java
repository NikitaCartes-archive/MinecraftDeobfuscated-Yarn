package net.minecraft.block.enums;

import net.minecraft.util.StringIdentifiable;

public enum SlabType implements StringIdentifiable {
	field_12679("top"),
	field_12681("bottom"),
	field_12682("double");

	private final String name;

	private SlabType(String name) {
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
