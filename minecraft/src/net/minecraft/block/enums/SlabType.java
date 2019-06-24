package net.minecraft.block.enums;

import net.minecraft.util.StringIdentifiable;

public enum SlabType implements StringIdentifiable {
	TOP("top"),
	BOTTOM("bottom"),
	DOUBLE("double");

	private final String name;

	private SlabType(String string2) {
		this.name = string2;
	}

	public String toString() {
		return this.name;
	}

	@Override
	public String asString() {
		return this.name;
	}
}
