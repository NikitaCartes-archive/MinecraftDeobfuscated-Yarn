package net.minecraft.block.enums;

import net.minecraft.util.StringIdentifiable;

public enum BedPart implements StringIdentifiable {
	HEAD("head"),
	FOOT("foot");

	private final String name;

	private BedPart(String string2) {
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
