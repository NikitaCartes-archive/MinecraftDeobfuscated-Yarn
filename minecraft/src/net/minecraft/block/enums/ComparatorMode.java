package net.minecraft.block.enums;

import net.minecraft.util.StringRepresentable;

public enum ComparatorMode implements StringRepresentable {
	field_12576("compare"),
	field_12578("subtract");

	private final String name;

	private ComparatorMode(String string2) {
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
