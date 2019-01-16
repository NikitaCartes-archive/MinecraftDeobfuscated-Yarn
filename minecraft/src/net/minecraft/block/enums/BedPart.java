package net.minecraft.block.enums;

import net.minecraft.util.StringRepresentable;

public enum BedPart implements StringRepresentable {
	field_12560("head"),
	field_12557("foot");

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
